package org.example.service;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

        responseDto = new UserResponseDto(1L, "Ivan", "ivan@test.com", 30);
    }

    @Test
    @DisplayName("create: saves user and returns response")
    void create_shouldSaveUser_and_returnResponse() {
        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(responseDto);

        UserResponseDto result = userService.create(requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(userMapper).toEntity(requestDto);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
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
    }

    @Test
    @DisplayName("getById: throws UserNotFoundException when user does not exist")
    void getById_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository).findById(99L);
        verifyNoInteractions(userMapper);
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
    }

    @Test
    @DisplayName("update: updates user and returns response when user exists")
    void update_shouldUpdateUser_and_returnResponse() {
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
        verify(userRepository).findById(1L);
        verify(userMapper).updateEntity(user, updateRequest);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
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
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("delete: deletes user when user exists")
    void delete_shouldDeleteUser_whenUserExists() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("delete: throws UserNotFoundException when user does not exist")
    void delete_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.delete(99L));

        verify(userRepository, never()).delete(any());
    }
}