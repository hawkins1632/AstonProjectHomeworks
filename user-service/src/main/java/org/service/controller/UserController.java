package org.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.service.dto.UserHalCollectionResponse;
import org.service.dto.UserHalResponse;
import org.service.dto.UserRequestDto;
import org.service.dto.UserResponseDto;
import org.service.exception.ErrorResponse;
import org.service.exception.ValidationErrorResponse;
import org.service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.service.hateoas.UserModelAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;

import java.util.List;

/**
 * REST-контроллер для управления пользователями.
 * Предоставляет HTTP API для создания, получения, обновления и удаления пользователей.
 */
@Tag(name = "Users", description = "Управление пользователями: создание, получение, обновление и удаление пользователей")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

    /**
     * Создаёт нового пользователя.
     *
     * @param requestDto данные нового пользователя
     * @return ответ с созданным пользователем и статусом 201 Created
     */
    @Operation(
            summary = "Создать пользователя",
            description = "Создаёт нового пользователя с указанными именем, электронной почтой и возрастом. "
                    + "Все поля обязательны и проходят валидацию. "
                    + "При успехе возвращает созданного пользователя со статусом 201 Created."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь успешно создан. Ответ возвращается в формате HAL+JSON "
                            + "и содержит HATEOAS-ссылки _links.",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = UserHalResponse.class),
                            examples = @ExampleObject(name = "created-user",
                                    summary = "HAL-ответ с созданным пользователем",
                                    value = """
                                            {
                                              "id": 1,
                                              "name": "Ivan",
                                              "email": "ivan@test.com",
                                              "age": 30,
                                              "_links": {
                                                "self": { "href": "http://localhost:8080/api/users/1" },
                                                "users": { "href": "http://localhost:8080/api/users" }
                                              }
                                            }
                                            """))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации тела запроса",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationErrorResponse.class),
                            examples = @ExampleObject(name = "validation-error",
                                    summary = "Ошибка валидации полей",
                                    value = """
                                            {
                                              "timestamp": "2026-10-04T12:00:00.123456",
                                              "status": 400,
                                              "errors": {
                                                "name": "Name cannot be empty",
                                                "email": "Invalid email format"
                                              }
                                            }
                                            """))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "duplicate-email",
                                    summary = "Дубликат электронной почты",
                                    value = """
                                            {
                                              "timestamp": "2026-10-04T12:00:00.123456",
                                              "status": 409,
                                              "message": "User with this email already exists"
                                            }
                                            """)))
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UserResponseDto>> create(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового пользователя. Все поля обязательны.",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserRequestDto.class),
                            examples = @ExampleObject(name = "create-request",
                                    summary = "Пример запроса на создание пользователя",
                                    value = """
                                            {
                                              "name": "Ivan",
                                              "email": "ivan@test.com",
                                              "age": 30
                                            }
                                            """))
            )
            @RequestBody UserRequestDto requestDto) {
        UserResponseDto responseDto = userService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).
                body(

                userModelAssembler.toModel(responseDto)

                );
    }

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return ответ с найденным пользователем и статусом 200 OK
     */
    @Operation(
            summary = "Получить пользователя по id",
            description = "Возвращает данные пользователя по его уникальному идентификатору. "
                    + "Если пользователь с указанным id не существует, возвращается 404 Not Found."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь найден. Ответ возвращается в формате HAL+JSON "
                            + "и содержит HATEOAS-ссылки _links.",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = UserHalResponse.class),
                            examples = @ExampleObject(name = "found-user",
                                    summary = "HAL-ответ с найденным пользователем",
                                    value = """
                                            {
                                              "id": 1,
                                              "name": "Ivan",
                                              "email": "ivan@test.com",
                                              "age": 30,
                                              "_links": {
                                                "self": { "href": "http://localhost:8080/api/users/1" },
                                                "users": { "href": "http://localhost:8080/api/users" }
                                              }
                                            }
                                            """))),
            @ApiResponse(responseCode = "400", description = "Неверный формат идентификатора (id должен быть целым числом)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "bad-id",
                                    summary = "Нечисловой идентификатор",
                                    value = """
                                            {
                                              "timestamp": "2026-10-04T12:00:00.123456",
                                              "status": 400,
                                              "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'"
                                            }
                                            """))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "user-not-found",
                                    summary = "Пользователь отсутствует в системе",
                                    value = """
                                            {
                                              "timestamp": "2026-10-04T12:00:00.123456",
                                              "status": 404,
                                              "message": "Failed to find user with id: 99"
                                            }
                                            """)))
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UserResponseDto>> getById(
            @Parameter(description = "Идентификатор пользователя (целое положительное число)", example = "1", required = true)
            @PathVariable Long id) {
        UserResponseDto responseDto = userService.getById(id);
        return ResponseEntity.ok(userModelAssembler.toModel(responseDto));
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return HAL-представление коллекции пользователей со статусом 200 OK
     */
    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает HAL-представление коллекции пользователей. "
                    + "Если пользователи отсутствуют, возвращается пустая HAL-коллекция."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список пользователей. Ответ возвращается в формате HAL+JSON: "
                            + "вложенный список в поле _embedded и HATEOAS-ссылки _links.",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = UserHalCollectionResponse.class),
                            examples = @ExampleObject(name = "users-list",
                                    summary = "HAL-ответ со списком пользователей",
                                    value = """
                                            {
                                              "_embedded": {
                                                "userResponseDtoList": [
                                                  {
                                                    "id": 1,
                                                    "name": "Ivan",
                                                    "email": "ivan@test.com",
                                                    "age": 30,
                                                    "_links": {
                                                      "self": { "href": "http://localhost:8080/api/users/1" },
                                                      "users": { "href": "http://localhost:8080/api/users" }
                                                    }
                                                  }
                                                ]
                                              },
                                              "_links": {
                                                "self": { "href": "http://localhost:8080/api/users" }
                                              }
                                            }
                                            """)))
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDto>>> getAll() {
        List<UserResponseDto> responseDtos = userService.getAll();
        return ResponseEntity.ok(

                userModelAssembler.toCollectionModel(responseDtos)

        );
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param id         уникальный идентификатор пользователя
     * @param requestDto новые данные пользователя
     * @return ответ с обновлённым пользователем и статусом 200 OK
     */
    @Operation(
            summary = "Обновить пользователя",
            description = "Полностью обновляет данные существующего пользователя. "
                    + "Все поля тела запроса обязательны и проходят валидацию. "
                    + "Если пользователь с указанным id не существует, возвращается 404 Not Found."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно обновлён. Ответ возвращается в формате HAL+JSON "
                            + "и содержит HATEOAS-ссылки _links.",
                    content = @Content(mediaType = MediaTypes.HAL_JSON_VALUE,
                            schema = @Schema(implementation = UserHalResponse.class),
                            examples = @ExampleObject(name = "updated-user",
                                    summary = "HAL-ответ с обновлённым пользователем",
                                    value = """
                                            {
                                              "id": 1,
                                              "name": "Ivan Petrov",
                                              "email": "ivan.petrov@test.com",
                                              "age": 31,
                                              "_links": {
                                                "self": { "href": "http://localhost:8080/api/users/1" },
                                                "users": { "href": "http://localhost:8080/api/users" }
                                              }
                                            }
                                            """))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации тела запроса или неверный формат идентификатора",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(oneOf = {ValidationErrorResponse.class, ErrorResponse.class}),
                            examples = @ExampleObject(name = "validation-error",
                                    summary = "Ошибка валидации полей",
                                    value = """
                                            {
                                              "timestamp": "2026-10-04T12:00:00.123456",
                                              "status": 400,
                                              "errors": {
                                                "email": "Invalid email format"
                                              }
                                            }
                                            """))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "duplicate-email",
                                    summary = "Дубликат электронной почты",
                                    value = """
                                            {
                                              "timestamp": "2026-10-04T12:00:00.123456",
                                              "status": 409,
                                              "message": "User with this email already exists"
                                            }
                                            """))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "user-not-found",
                                    summary = "Пользователь отсутствует в системе",
                                    value = """
                                            {
                                              "timestamp": "2026-10-04T12:00:00.123456",
                                              "status": 404,
                                              "message": "Failed to find user with id: 99"
                                            }
                                            """)))
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UserResponseDto>> update(
            @Parameter(description = "Идентификатор пользователя (целое положительное число)", example = "1", required = true)
            @PathVariable Long id,
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные пользователя. Все поля обязательны.",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserRequestDto.class),
                            examples = @ExampleObject(name = "update-request",
                                    summary = "Пример запроса на обновление пользователя",
                                    value = """
                                            {
                                              "name": "Ivan Petrov",
                                              "email": "ivan.petrov@test.com",
                                              "age": 31
                                            }
                                            """))
            )
            @RequestBody UserRequestDto requestDto) {
        UserResponseDto responseDto = userService.update(id, requestDto);
        return ResponseEntity.ok(

                userModelAssembler.toModel(responseDto)

        );
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return ответ без тела и статусом 204 No Content
     */
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по его уникальному идентификатору. "
                    + "При успехе возвращается 204 No Content без тела ответа. "
                    + "Если пользователь с указанным id не существует, возвращается 404 Not Found."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удалён"),
            @ApiResponse(responseCode = "400", description = "Неверный формат идентификатора (id должен быть целым числом)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "bad-id",
                                    summary = "Нечисловой идентификатор",
                                    value = """
                                            {
                                              "timestamp": "2026-10-04T12:00:00.123456",
                                              "status": 400,
                                              "message": "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'"
                                            }
                                            """))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "user-not-found",
                                    summary = "Пользователь отсутствует в системе",
                                    value = """
                                            {
                                              "timestamp": "2026-10-04T12:00:00.123456",
                                              "status": 404,
                                              "message": "Failed to find user with id: 99"
                                            }
                                            """)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Идентификатор пользователя (целое положительное число)", example = "1", required = true)
            @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}