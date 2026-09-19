package org.example.controller;

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
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto requestDto){
        UserResponseDto responseDto = userService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return ответ с найденным пользователем и статусом 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id){
        UserResponseDto responseDto = userService.getById(id);
        return ResponseEntity.ok(responseDto);
    }
    /**
     * Возвращает список всех пользователей.
     *
     * @return ответ со списком пользователей и статусом 200 OK
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll(){
        List<UserResponseDto> responseDtos = userService.getAll();
        return ResponseEntity.ok(responseDtos);
    }
    /**
     * Обновляет данные существующего пользователя.
     *
     * @param id уникальный идентификатор пользователя
     * @param requestDto новые данные пользователя
     * @return ответ с обновлённым пользователем и статусом 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id, @Valid @RequestBody UserRequestDto requestDto){
        UserResponseDto responseDto = userService.update(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }
    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return ответ без тела и статусом 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
