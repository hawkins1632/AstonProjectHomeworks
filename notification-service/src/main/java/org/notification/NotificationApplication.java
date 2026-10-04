package org.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в приложение notification-service.
 * <p>
 * Запускает Spring Boot приложение, которое слушает Kafka-события
 * пользователей и отправляет email-уведомления.
 */
@SpringBootApplication
public class NotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class);
    }
}