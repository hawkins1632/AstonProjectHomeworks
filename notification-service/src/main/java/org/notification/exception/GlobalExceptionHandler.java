package org.notification.exception;

import lombok.extern.slf4j.Slf4j;
import org.notification.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

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