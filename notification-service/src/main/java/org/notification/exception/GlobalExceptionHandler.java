package org.notification.exception;

import lombok.extern.slf4j.Slf4j;
import org.notification.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    /**
     * Обрабатывает ошибки валидации тела запроса (@Valid).
     * <p>
     * Возвращает HTTP 400 Bad Request с телом {@link ErrorDto} в формате,
     * заявленном в OpenAPI-спецификации.
     *
     * @param ex исключение валидации
     * @return ответ с телом {@link ErrorDto} и статусом 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidation(MethodArgumentNotValidException ex) {
        ServiceError error = ServiceError.VALIDATION_ERROR;
        log.error("Ошибка валидации запроса: {}", ex.getMessage());
        return ResponseEntity.status(error.getStatus())
                .body(new ErrorDto(error.getSubject(), error.getMessage(), LocalDateTime.now()));
    }

    /**
     * Обрабатывает ошибки чтения тела запроса
     * (например, некорректный или неполный JSON).
     *
     * @param ex исключение
     * @return ответ с телом {@link ErrorDto} и статусом 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        ServiceError error = ServiceError.VALIDATION_ERROR;
        log.error("Некорректное тело запроса: {}", ex.getMessage());
        return ResponseEntity.status(error.getStatus())
                .body(new ErrorDto(error.getSubject(), error.getMessage(), LocalDateTime.now()));
    }
}