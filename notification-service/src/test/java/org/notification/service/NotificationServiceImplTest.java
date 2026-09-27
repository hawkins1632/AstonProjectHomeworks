package org.notification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.notification.exception.ServiceError;
import org.notification.exception.ServiceException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private static final String FROM = "noreply@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "from", FROM);
    }

    @Test
    void sendCreated_shouldSendEmailWithCorrectFields() {
        notificationService.sendCreated("user@example.com");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getFrom()).isEqualTo(FROM);
        assertThat(message.getTo()).containsExactly("user@example.com");
        assertThat(message.getSubject()).isEqualTo("Аккаунт успешно создан");
        assertThat(message.getText()).contains("Ваш аккаунт на сайте был успешно создан");
    }

    @Test
    void sendUpdated_shouldSendEmailWithCorrectFields() {
        notificationService.sendUpdated("user@example.com");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getSubject()).isEqualTo("Аккаунт успешно изменён");
        assertThat(message.getText()).contains("Ваш аккаунт был изменён");
    }

    @Test
    void sendDeleted_shouldSendEmailWithCorrectFields() {
        notificationService.sendDeleted("user@example.com");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getSubject()).isEqualTo("Аккаунт удалён");
        assertThat(message.getText()).contains("Ваш аккаунт был удалён");
    }

    @Test
    void send_shouldWrapMailExceptionIntoServiceException() {
        doThrow(new MailException("smtp down") {})
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertThatThrownBy(() -> notificationService.sendCreated("user@example.com"))
                .isInstanceOf(ServiceException.class)
                .extracting(e -> ((ServiceException) e).getError())
                .isEqualTo(ServiceError.MAIL_SEND_ERROR);
    }
}