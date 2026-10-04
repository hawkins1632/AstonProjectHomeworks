package org.notification.exception;

import lombok.extern.slf4j.Slf4j;
import org.notification.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Глобальный обработчик исключений REST-контроллеров notification-service.
 * <p>
 * Перехватывает бизнес-исключения {@link ServiceException} и преобразует их
 * в единообразный JSON-ответ {@link ErrorDto} с корректным HTTP-статусом.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение {@link ServiceException}.
     * <p>
     * Формирует тело ответа на основе {@link ServiceError}, извлечённой
     * из исключения, и возвращает HTTP-статус, заданный в этой ошибке.
     *
     * @param ex исключение сервиса
     * @return ответ с телом {@link ErrorDto} и соответствующим HTTP-статусом
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorDto> handleService(ServiceException ex) {
        ServiceError error = ex.getError();
        ErrorDto body = new ErrorDto(
                error.getSubject(),
                error.getMessage(),
                LocalDateTime.now()
        );
        log.error("Ошибка: {}:{}", error.getSubject(), error.getMessage());
        return ResponseEntity.status(error.getStatus()).body(body);
    }
}