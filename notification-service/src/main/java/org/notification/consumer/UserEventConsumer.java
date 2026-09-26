package org.notification.consumer;

import event.UserEvent;
import lombok.RequiredArgsConstructor;
import org.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${app.kafka.topic}")
    public void consume(UserEvent event) {
        notificationService.send(event);
    }
}