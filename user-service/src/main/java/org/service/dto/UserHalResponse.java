package org.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * HAL-представление пользователя для OpenAPI-документации.
 * <p>
 * Отражает фактический формат ответа {@code application/hal+json}:
 * данные пользователя на верхнем уровне и HATEOAS-ссылки в поле {@code _links}.
 */
@Schema(description = "HAL-представление пользователя (HATEOAS): данные пользователя и навигационные ссылки _links")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserHalResponse {
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

    /**
     * HATEOAS-ссылки.
     */
    @JsonProperty("_links")
    @Schema(description = "HATEOAS-ссылки: self — сам пользователь, users — коллекция пользователей")
    private Map<String, HalLink> _links;
}