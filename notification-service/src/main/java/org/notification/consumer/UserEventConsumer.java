package org.notification.consumer;

import lombok.extern.slf4j.Slf4j;
import org.events.UserEvent;
import lombok.RequiredArgsConstructor;
import org.events.UserEventType;
import org.notification.service.NotificationServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final NotificationServiceImpl notificationService;

    @KafkaListener(topics = "${app.kafka.topic}")
    public void consume(UserEvent event) {
        String email = event.email();
        UserEventType type = event.type();
        log.info("Получено сообщение из Kafka (email = {}, type = {}", email, type);
        switch (type) {
            case CREATED -> notificationService.sendCreated(email);
            case UPDATED -> notificationService.sendUpdated(email);
            case DELETED -> notificationService.sendDeleted(email);
        }
        log.info("Обработано сообщение из Kafka (email = {}, type = {}", email, type);
    }
}