package org.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на создание или обновление пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Данные для создания или обновления пользователя")
public class UserRequestDto {
    /**
     * Имя пользователя.
     */
    @Schema(description = "Имя пользователя. Обязательное поле, не может быть пустым.",
            example = "Ivan", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    @NotBlank(message = "Name cannot be empty")
    @Size(max = 100)
    private String name;

    /**
     * Электронная почта пользователя.
     */
    @Schema(description = "Электронная почта пользователя. Обязательное поле, должна быть валидным адресом.",
            example = "ivan@test.com", requiredMode = Schema.RequiredMode.REQUIRED,
            format = "email", maxLength = 100)
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;

    /**
     * Возраст пользователя.
     */
    @Schema(description = "Возраст пользователя. Обязательное целое число; диапазон значений не ограничен.",
            example = "30", requiredMode = Schema.RequiredMode.REQUIRED, format = "int32")
    @NotNull(message = "Age cannot be null")
    private Integer age;
}