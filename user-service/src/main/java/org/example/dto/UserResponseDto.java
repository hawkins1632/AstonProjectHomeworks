package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа с данными пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Данные пользователя")
public class UserResponseDto {
    /**
     * Идентификатор пользователя.
     */
    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long id;
    /**
     * Имя пользователя.
     */
    @Schema(description = "Имя пользователя", example = "Ivan")
    private String name;
    /**
     * Электронная почта пользователя.
     */
    @Schema(description = "Электронная почта", example = "ivan@test.com")
    private String email;
    /**
     * Возраст пользователя.
     */
    @Schema(description = "Возраст", example = "30")
    private Integer age;
}
