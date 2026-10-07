package org.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HATEOAS-ссылка в составе HAL-ответа.
 */
@Schema(description = "HATEOAS-ссылка")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HalLink {
    /**
     * URI ссылки.
     */
    @Schema(description = "Относительный или абсолютный URI ссылки",
            example = "http://localhost:8080/api/users/1")
    private String href;
}