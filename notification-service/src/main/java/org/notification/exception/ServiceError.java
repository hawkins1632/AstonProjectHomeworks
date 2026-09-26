package org.notification.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ServiceError {
    VALIDATION_ERROR(
            "notification.error.validation",
            "Ошибка валидации запроса",
            HttpStatus.BAD_REQUEST
    ),
    INVALID_NOTIFICATION_TYPE(
            "notification.error.invalid-type",
            "Недопустимый тип уведомления",
            HttpStatus.BAD_REQUEST
    ),
    MAIL_SEND_ERROR(
            "notification.error.mail-send",
            "Не удалось отправить письмо",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),
    INTERNAL_ERROR(
            "notification.error.internal",
            "Внутренняя ошибка сервера",
            HttpStatus.INTERNAL_SERVER_ERROR
    );

    private final String subject;
    private final String message;
    private final HttpStatus status;
}