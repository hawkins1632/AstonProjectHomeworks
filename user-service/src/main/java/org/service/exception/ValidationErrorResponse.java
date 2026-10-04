package org.service.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Тело ответа об ошибке валидации полей запроса.
 * <p>
 * Возвращается при {@code 400 Bad Request}, когда тело запроса не проходит
 * Bean Validation: поле {@code errors} содержит соответствие «имя поля — сообщение об ошибке».
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Тело ответа об ошибке валидации полей запроса")
public class ValidationErrorResponse {
    /**
     * Время возникновения ошибки.
     */
    @Schema(description = "Время возникновения ошибки", example = "2026-10-04T12:00:00.123456")
    private LocalDateTime timestamp;

    /**
     * HTTP-статус ошибки.
     */
    @Schema(description = "HTTP-статус ошибки", example = "400")
    private int status;

    /**
     * Ошибки валидации по полям: ключ — имя поля, значение — сообщение об ошибке.
     */
    @Schema(description = "Ошибки валидации по полям: ключ — имя поля, значение — сообщение об ошибке",
            example = "{\"name\": \"Name cannot be empty\"}")
    private Map<String, String> errors;
}