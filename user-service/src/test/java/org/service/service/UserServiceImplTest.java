package org.service.service;

import org.service.dto.UserRequestDto;
import org.service.dto.UserResponseDto;
import org.service.exception.UserNotFoundException;
import org.service.kafka.UserEventProducer;
import org.service.mapper.UserMapper;
import org.service.model.User;
import org.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

        responseDto = new UserResponseDto(1L, "Ivan", "ivan@test.com", 30);
    }

    @Test
    @DisplayName("create: saves user, sends CREATED event and returns response")
    void create_shouldSaveUser_sendEvent_andReturnResponse() {
        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(responseDto);

        UserResponseDto result = userService.create(requestDto);

        assertThat(result).isEqualTo(responseDto);

        var inOrder = inOrder(userMapper, userRepository, userEventProducer);
        inOrder.verify(userMapper).toEntity(requestDto);
        inOrder.verify(userRepository).save(user);
        inOrder.verify(userEventProducer).sendCreated(user);
        inOrder.verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("getById: returns response when user exists")
    void getById_shouldReturnResponse_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getById(1L);

        assertThat(result).isEqualTo(responseDto);
        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(user);
        verifyNoInteractions(userEventProducer);
    }

    @Test
    @DisplayName("getById: throws UserNotFoundException when user does not exist")
    void getById_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository).findById(99L);
        verifyNoInteractions(userMapper, userEventProducer);
    }

    @Test
    @DisplayName("getAll: returns responses list when users exist")
    void getAll_shouldReturnResponsesList_whenUsersExist() {
        List<User> users = List.of(user);
        List<UserResponseDto> responses = List.of(responseDto);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponseList(users)).thenReturn(responses);

        List<UserResponseDto> result = userService.getAll();

        assertThat(result).hasSize(1).containsExactly(responseDto);
        verify(userRepository).findAll();
        verify(userMapper).toResponseList(users);
        verifyNoInteractions(userEventProducer);
    }

    @Test
    @DisplayName("getAll: returns empty list when no users")
    void getAll_shouldReturnEmptyList_whenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());
        when(userMapper.toResponseList(List.of())).thenReturn(List.of());

        List<UserResponseDto> result = userService.getAll();

        assertThat(result).isEmpty();
        verify(userRepository).findAll();
        verify(userMapper).toResponseList(List.of());
        verifyNoInteractions(userEventProducer);
    }

    @Test
    @DisplayName("update:updates user, sends UPDATED event and returns response")
    void update_shouldUpdateUser_sendEvent_andReturnResponse() {
        UserRequestDto updateRequest = new UserRequestDto("Petr", "petr@test.com", 35);
        UserResponseDto updatedResponse = new UserResponseDto(1L, "Petr", "petr@test.com", 35);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            UserRequestDto dto = invocation.getArgument(1);
            u.setName(dto.getName());
            u.setEmail(dto.getEmail());
            u.setAge(dto.getAge());
            return null;
        }).when(userMapper).updateEntity(any(User.class), any(UserRequestDto.class));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(updatedResponse);

        UserResponseDto result = userService.update(1L, updateRequest);

        assertThat(result).isEqualTo(updatedResponse);
        assertThat(user.getName()).isEqualTo("Petr");
        assertThat(user.getEmail()).isEqualTo("petr@test.com");
        assertThat(user.getAge()).isEqualTo(35);

        var inOrder = inOrder(userRepository, userMapper, userEventProducer);
        inOrder.verify(userRepository).findById(1L);
        inOrder.verify(userMapper).updateEntity(user, updateRequest);
        inOrder.verify(userRepository).save(user);
        inOrder.verify(userEventProducer).sendUpdated(user);
        inOrder.verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("update: throws UserNotFoundException when user does not exist")
    void update_shouldThrowException_whenUserDoesNotExist() {
        UserRequestDto updateRequest = new UserRequestDto("Petr", "petr@test.com", 35);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, updateRequest))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository).findById(99L);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper, userEventProducer);
    }

    @Test
    @DisplayName("delete: deletes user when user exists")
    void delete_shouldDeleteUser_whenUserExists() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        var inOrder = inOrder(userRepository, userEventProducer);
        inOrder.verify(userRepository).findById(1L);
        inOrder.verify(userRepository).delete(user);
        inOrder.verify(userEventProducer).sendDeleted(user);
    }

    @Test
    @DisplayName("delete: throws UserNotFoundException when user does not exist")
    void delete_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(99L);
        verify(userRepository, never()).delete(any());
        verifyNoInteractions(userEventProducer);
    }
}