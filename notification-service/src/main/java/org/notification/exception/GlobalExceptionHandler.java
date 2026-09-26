package org.notification.exception;

import org.notification.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorDto> handleService(ServiceException ex) {
        ServiceError error = ex.getError();
        ErrorDto body = new ErrorDto(
                error.getSubject(),
                error.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(error.getStatus()).body(body);
    }
}