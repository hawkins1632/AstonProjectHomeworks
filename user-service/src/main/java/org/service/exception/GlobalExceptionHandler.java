package org.service.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений REST-контроллеров.
 * <p>
 * Перехватывает исключения валидации, бизнес-исключения и другие ошибки,
 * преобразуя их в единообразный JSON-ответ:
 * <ul>
 *     <li>{@link ErrorResponse} — стандартная ошибка
 *     ({@code timestamp}, {@code status}, {@code message});</li>
 *     <li>{@link ValidationErrorResponse} — ошибка валидации полей запроса
 *     ({@code timestamp}, {@code status}, {@code errors}).</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Обрабатывает ошибки валидации тела запроса (@Valid).
     *
     * @param ex исключение валидации
     * @return HTTP 400 Bad Request с полем {@code errors} (поле -> сообщение)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(new ValidationErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), fieldErrors));
    }

    /**
     * Обрабатывает исключения {@link IllegalArgumentException}.
     *
     * @param ex исключение
     * @return HTTP 400 Bad Request с сообщением об ошибке
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    /**
     * Обрабатывает исключение {@link UserNotFoundException}.
     *
     * @param ex исключение
     * @return HTTP 404 Not Found с сообщением об ошибке
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    /**
     * Обрабатывает ошибки валидации constraint violation (например, для @PathVariable).
     *
     * @param ex исключение
     * @return HTTP 400 Bad Request с сообщением об ошибке
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest()
                .body(errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    /**
     * Обрабатывает ошибку приведения типа path-параметра
     * (например, нечисловой идентификатор в {@code /api/users/abc}).
     *
     * @param ex исключение
     * @return HTTP 400 Bad Request с сообщением об ошибке
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    /**
     * Обрабатывает ошибки чтения тела запроса
     * (например, некорректный или неполный JSON).
     *
     * @param ex исключение
     * @return HTTP 400 Bad Request с сообщением об ошибке
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    /**
     * Обрабатывает нарушение целостности данных.
     * <p>
     * HTTP 409 Conflict возвращается только при нарушении уникальности email
     * (constraint {@code users_email_key}) — попытка создать/обновить пользователя
     * с уже существующим email. Прочие нарушения целостности (NOT NULL, длина поля
     * и т.п.) — это внутренние ошибки, поэтому возвращается HTTP 500.
     *
     * @param ex исключение
     * @return HTTP 409 Conflict при дубликате email, иначе HTTP 500
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Throwable cause = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause() : ex;
        String message = cause.getMessage() == null ? "" : cause.getMessage();

        if (message.contains("users_email_key")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(errorResponse(HttpStatus.CONFLICT, "User with this email already exists"));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"));
    }

    private ErrorResponse errorResponse(HttpStatus status, String message) {
        return new ErrorResponse(LocalDateTime.now(), status.value(), message);
    }
}