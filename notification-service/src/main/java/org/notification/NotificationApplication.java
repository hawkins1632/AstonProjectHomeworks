package org.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Точка входа в приложение notification-service.
 * <p>
 * Запускает Spring Boot приложение, которое слушает Kafka-события
 * пользователей и отправляет email-уведомления.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class);
    }
}