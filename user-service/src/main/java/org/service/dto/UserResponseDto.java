package org.service.dto;

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
    @Schema(description = "Уникальный идентификатор пользователя",
            example = "1", requiredMode = Schema.RequiredMode.REQUIRED, format = "int64")
    private Long id;

    /**
     * Имя пользователя.
     */
    @Schema(description = "Имя пользователя", example = "Ivan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * Электронная почта пользователя.
     */
    @Schema(description = "Электронная почта пользователя",
            example = "ivan@test.com", requiredMode = Schema.RequiredMode.REQUIRED, format = "email")
    private String email;

    /**
     * Возраст пользователя.
     */
    @Schema(description = "Возраст пользователя",
            example = "30", requiredMode = Schema.RequiredMode.REQUIRED, format = "int32")
    private Integer age;
}