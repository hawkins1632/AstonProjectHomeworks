package org.example.dto;

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
public class UserRequestDto {
    /** Имя пользователя. */
    @NotBlank(message = "Name cannot be empty")
    @Size(max = 100)
    private String name;
    /** Электронная почта пользователя. */
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;
    /** Возраст пользователя. */
    @NotNull(message = "Age cannot be null")
    private Integer age;
}
