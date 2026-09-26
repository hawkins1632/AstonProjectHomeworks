package org.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.notification.exception.ServiceError;
import org.notification.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    private void send(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        try {
            mailSender.send(message);
        }
        catch (MailException e) {
            throw new ServiceException(ServiceError.MAIL_SEND_ERROR);
        }
    }

    @Override
    public void sendCreated(String email) {
        send(email, "Аккаунт успешно создан", "Здравствуйте! Ваш аккаунт на сайте был успешно создан.");
    }

    @Override
    public void sendUpdated(String email) {
        send(email, "Аккаунт успешно изменён", "Здравствуйте! Ваш аккаунт был изменён.");
    }

    @Override
    public void sendDeleted(String email) {
        send(email, "Аккаунт удалён", "Здравствуйте! Ваш аккаунт был удалён.");
    }
}