package org.notification.exception;

import org.junit.jupiter.api.Test;
import org.notification.dto.ErrorDto;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private MethodArgumentNotValidException validationException() throws NoSuchMethodException {
        MethodParameter parameter = new MethodParameter(
                SampleController.class.getMethod("post", String.class), 0);
        return new MethodArgumentNotValidException(parameter, new BeanPropertyBindingResult(new Object(), "dto"));
    }

    @Test
    void handleValidation_shouldReturnBadRequestWithErrorDto() throws NoSuchMethodException {
        ResponseEntity<ErrorDto> response = handler.handleValidation(validationException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSubject()).isEqualTo(ServiceError.VALIDATION_ERROR.getSubject());
        assertThat(response.getBody().getMessage()).isEqualTo(ServiceError.VALIDATION_ERROR.getMessage());
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleMessageNotReadable_shouldReturnBadRequestWithErrorDto() {
        ResponseEntity<ErrorDto> response =
                handler.handleMessageNotReadable(
                        new HttpMessageNotReadableException("bad json", mock(HttpInputMessage.class)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSubject()).isEqualTo(ServiceError.VALIDATION_ERROR.getSubject());
        assertThat(response.getBody().getMessage()).isEqualTo(ServiceError.VALIDATION_ERROR.getMessage());
    }

    static class SampleController {
        public void post(String body) {
        }
    }
}