package org.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * HAL-представление коллекции пользователей для OpenAPI-документации.
 * <p>
 * Отражает фактический формат ответа {@code application/hal+json}:
 * вложенный список пользователей в поле {@code _embedded} и HATEOAS-ссылки в поле {@code _links}.
 */
@Schema(description = "HAL-представление коллекции пользователей (HATEOAS)")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserHalCollectionResponse {
    /**
     * Вложенный список пользователей.
     */
    @JsonProperty("_embedded")
    @Schema(description = "Вложенный список пользователей (ключ — userResponseDtoList)")
    private Map<String, List<UserHalResponse>> _embedded;

    /**
     * HATEOAS-ссылки.
     */
    @JsonProperty("_links")
    @Schema(description = "HATEOAS-ссылки: self — текущая коллекция")
    private Map<String, HalLink> _links;
}