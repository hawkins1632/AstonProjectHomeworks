package org.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.service.dto.UserRequestDto;
import org.service.dto.UserResponseDto;
import org.service.exception.UserNotFoundException;
import org.service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @Test
    void getAll_shouldReturnListOfUsers() throws Exception {
        UserResponseDto dto = new UserResponseDto(1L, "Alice", "alice@mail.com", 30);
        when(userService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].email").value("alice@mail.com"))
                .andExpect(jsonPath("$[0].age").value(30));

        verify(userService).getAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoUsers() throws Exception {
        when(userService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getById_shouldReturnUser() throws Exception {
        UserResponseDto dto = new UserResponseDto(1L, "Bob", "bob@mail.com", 25);
        when(userService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@mail.com"))
                .andExpect(jsonPath("$.age").value(25));

        verify(userService).getById(1L);
    }

    @Test
    void getById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        when(userService.getById(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Failed to find user with id: 99"));
    }

    @Test
    void create_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        UserRequestDto request = new UserRequestDto("", "test@mail.com", 20);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.name").value("Name cannot be empty"));
    }

    @Test
    void create_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        UserRequestDto request = new UserRequestDto("Alice", "not-an-email", 20);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void create_shouldReturnBadRequest_whenAgeIsNull() throws Exception {
        UserRequestDto request = new UserRequestDto("Alice", "alice@mail.com", null);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.age").value("Age cannot be null"));
    }

    @Test
    void create_shouldReturnCreatedStatus() throws Exception {
        UserRequestDto request = new UserRequestDto("Charlie", "charlie@mail.com", 35);
        UserResponseDto response = new UserResponseDto(2L, "Charlie", "charlie@mail.com", 35);

        when(userService.create(any(UserRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Charlie"))
                .andExpect(jsonPath("$.email").value("charlie@mail.com"))
                .andExpect(jsonPath("$.age").value(35));

        verify(userService).create(any(UserRequestDto.class));
    }

    @Test
    void update_shouldReturnUpdatedUser() throws Exception {
        UserRequestDto request = new UserRequestDto("David", "david@mail.com", 40);
        UserResponseDto response = new UserResponseDto(1L, "David", "david@mail.com", 40);

        when(userService.update(eq(1L), any(UserRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("David"))
                .andExpect(jsonPath("$.email").value("david@mail.com"))
                .andExpect(jsonPath("$.age").value(40));

        verify(userService).update(eq(1L), any(UserRequestDto.class));
    }

    @Test
    void update_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        UserRequestDto request = new UserRequestDto("", "david@mail.com", 40);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name cannot be empty"));

        verifyNoInteractions(userService);
    }

    @Test
    void update_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        UserRequestDto request = new UserRequestDto("David", "david@mail.com", 40);
        when(userService.update(eq(99L), any(UserRequestDto.class)))
                .thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Failed to find user with id: 99"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }

    @Test
    void delete_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        doThrow(new UserNotFoundException(99L)).when(userService).delete(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Failed to find user with id: 99"));
    }
}
