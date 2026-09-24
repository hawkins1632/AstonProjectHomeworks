package org.notification.service;

import event.EventType;
import event.UserEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
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

        if (event.getEvent() == EventType.USER_CREATED) {
            subject = "Аккаунт успешно создан";
            text = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
        } else if (event.getEvent() == EventType.USER_DELETED) {
            subject = "Аккаунт удалён";
            text = "Здравствуйте! Ваш аккаунт был удалён.";
        } else {
            throw new IllegalArgumentException(
                    "Unsupported event type: " + event.getEvent()
            );
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.getEmail());
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}