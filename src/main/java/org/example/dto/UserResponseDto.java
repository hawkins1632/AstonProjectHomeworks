package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO для ответа с данными пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    /** Идентификатор пользователя. */
    private Long id;
    /** Имя пользователя. */
    private String name;
    /** Электронная почта пользователя. */
    private String email;
    /** Возраст пользователя. */
    private Integer age;
}
