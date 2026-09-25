package org.example.service;

import event.EventType;
import event.UserEvent;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.exception.UserNotFoundException;
import org.example.kafka.UserEventProducer;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserEventProducer userEventProducer;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDto requestDto;
    private UserResponseDto responseDto;

    @BeforeEach
    void setUp() {
        user = new User("Ivan", "ivan@test.com", 30);
        user.setId(1L);
        user.setCreatedAt(LocalDateTime.now());

        requestDto = new UserRequestDto("Ivan", "ivan@test.com", 30);

        responseDto =
                new UserResponseDto(1L, "Ivan", "ivan@test.com", 30);
    }

    @Test
    @DisplayName("create: saves user, sends Kafka event and returns response")
    void create_shouldSaveUser_sendEvent_andReturnResponse() {

        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(responseDto);

        UserResponseDto result = userService.create(requestDto);

        assertThat(result).isEqualTo(responseDto);

        verify(userMapper).toEntity(requestDto);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);

        ArgumentCaptor<UserEvent> eventCaptor =
                ArgumentCaptor.forClass(UserEvent.class);

        verify(userEventProducer).send(eventCaptor.capture());

        UserEvent event = eventCaptor.getValue();

        assertThat(event.getEvent()).isEqualTo(EventType.USER_CREATED);
        assertThat(event.getEmail()).isEqualTo("ivan@test.com");
    }

    @Test
    @DisplayName("delete: finds user, deletes user and sends Kafka event")
    void delete_shouldDeleteUser_andSendEvent() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).deleteById(1L);

        ArgumentCaptor<UserEvent> eventCaptor =
                ArgumentCaptor.forClass(UserEvent.class);

        verify(userEventProducer).send(eventCaptor.capture());

        UserEvent event = eventCaptor.getValue();

        assertThat(event.getEvent()).isEqualTo(EventType.USER_DELETED);
        assertThat(event.getEmail()).isEqualTo("ivan@test.com");
    }

    @Test
    @DisplayName("delete: throws exception when user does not exist")
    void delete_shouldThrowException_whenUserDoesNotExist() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(99L);
        verify(userRepository, never()).deleteById(anyLong());
        verifyNoInteractions(userEventProducer);
    }
}