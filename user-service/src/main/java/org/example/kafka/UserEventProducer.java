package org.example.kafka;

import event.EventType;
import event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${app.kafka.topic}")
    public String TOPIC;

    public void sendCreated(User user) {
        send(user, EventType.CREATED);
    }
    public void sendDeleted(User user) {
        send(user, EventType.DELETED);
    }
    public void sendUpdated(User user) {
        send(user, EventType.UPDATED);
    }

    private void send(User user, EventType type) {
        try {
            // .get() делаем синхронную отправку: если Kafka недоступна,
            // метод бросит исключение, и транзакция БД сможет откатиться.
            String email = user.getEmail();
            kafkaTemplate.send(TOPIC, email, new UserEvent(user.getId(), user.getEmail(), type))
                    .get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send user event to Kafka", e);
        }
    }
}