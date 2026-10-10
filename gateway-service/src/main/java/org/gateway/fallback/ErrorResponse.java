package org.gateway.fallback;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Стандартное тело ответа об ошибке")
public record ErrorResponse(
        @Schema(description = "Время возникновения ошибки", example = "2026-10-10T12:00:00.123456")
        LocalDateTime timestamp,
        @Schema(description = "HTTP-статус ошибки", example = "503")
        int status,
        @Schema(description = "Сообщение об ошибке", example = "Service is temporarily unavailable. Please try again later.")
        String message) {
}
