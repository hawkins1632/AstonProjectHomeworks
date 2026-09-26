package org.notification.service;

import event.EventType;
import event.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;
    private final String from;

    public NotificationService(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String from
    ) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void send(UserEvent event) {
        String subject;
        String text;

        if (event.type() == EventType.CREATED) {
            subject = "Аккаунт успешно создан";
            text = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
        } else if (event.type() == EventType.DELETED) {
            subject = "Аккаунт удалён";
            text = "Здравствуйте! Ваш аккаунт был удалён.";
        } else if (event.type() == EventType.UPDATED) {
            subject = "Аккаунт изменён";
            text = "Здравствуйте! Ваш аккаунт был изменён.";
        } else {
            throw new IllegalArgumentException(
                    "Unsupported event type: " + event.type()
            );
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.email());
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}