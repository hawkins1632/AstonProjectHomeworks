package org.notification.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Исключение бизнес-логики notification-service.
 * <p>
 * Содержит описание ошибки {@link ServiceError}, которое используется
 * глобальным обработчиком для формирования ответа клиенту.
 */
@Getter
@RequiredArgsConstructor
public class ServiceException extends RuntimeException {
    private final ServiceError error;
}
