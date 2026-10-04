package org.service.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Стандартное тело ответа об ошибке.
 * <p>
 * Возвращается при ошибках, не связанных с валидацией полей запроса:
 * {@code 400 Bad Request} (неверный формат данных, некорректный идентификатор)
 * и {@code 404 Not Found} (пользователь не найден).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Стандартное тело ответа об ошибке")
public class ErrorResponse {
    /**
     * Время возникновения ошибки.
     */
    @Schema(description = "Время возникновения ошибки", example = "2026-10-04T12:00:00.123456")
    private LocalDateTime timestamp;

    /**
     * HTTP-статус ошибки.
     */
    @Schema(description = "HTTP-статус ошибки", example = "404")
    private int status;

    /**
     * Сообщение об ошибке.
     */
    @Schema(description = "Сообщение об ошибке", example = "Failed to find user with id: 99")
    private String message;
}