package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления пользователями.
 * Предоставляет HTTP API для создания, получения, обновления и удаления пользователей.
 */
@Tag(name = "Users", description = "Управление пользователями")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Создаёт нового пользователя.
     *
     * @param requestDto данные нового пользователя
     * @return ответ с созданным пользователем и статусом 201 Created
     */
    @Operation(summary = "Создать пользователя")
    @ApiResponse(responseCode = "201", description = "Пользователь создан")
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto responseDto = userService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return ответ с найденным пользователем и статусом 200 OK
     */
    @Operation(summary = "Получить пользователя по id")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(
            @Parameter(description = "Идентификатор пользователя", example = "1", required = true)
            @PathVariable Long id) {
        UserResponseDto responseDto = userService.getById(id);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return ответ со списком пользователей и статусом 200 OK
     */
    @Operation(summary = "Получить всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        List<UserResponseDto> responseDtos = userService.getAll();
        return ResponseEntity.ok(responseDtos);
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param id         уникальный идентификатор пользователя
     * @param requestDto новые данные пользователя
     * @return ответ с обновлённым пользователем и статусом 200 OK
     */
    @Operation(summary = "Обновить пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь обновлён")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(
            @Parameter(description = "Идентификатор пользователя", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto responseDto = userService.update(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return ответ без тела и статусом 204 No Content
     */
    @Operation(summary = "Удалить пользователя")
    @ApiResponse(responseCode = "204", description = "Пользователь удалён")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Идентификатор пользователя", example = "1", required = true)
            @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
