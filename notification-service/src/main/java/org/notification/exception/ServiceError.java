package org.notification.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Перечень бизнес-ошибок сервиса уведомлений.
 * <p>
 * Каждая ошибка содержит код-субъект,
 * текст сообщения и соответствующий HTTP-статус.
 */
@RequiredArgsConstructor
@Getter
public enum ServiceError {
    /**
     * Ошибка валидации входящего запроса.
     */
    VALIDATION_ERROR(
            "notification.error.validation",
            "Ошибка валидации запроса",
            HttpStatus.BAD_REQUEST
    ),

    /**
     * Ошибка отправки email-сообщения.
     */
    MAIL_SEND_ERROR(
            "notification.error.mail-send",
            "Не удалось отправить письмо",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),

    /**
     * Внутренняя ошибка сервера.
     */
    INTERNAL_ERROR(
            "notification.error.internal",
            "Внутренняя ошибка сервера",
            HttpStatus.INTERNAL_SERVER_ERROR
    );

    private final String subject;
    private final String message;
    private final HttpStatus status;
}