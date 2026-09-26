package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserEvent;
import org.example.model.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    public static final String TOPIC = "user-events";

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void sendCreated(User user) {
        send(UserEvent.created(user));
    }

    public void sendDeleted(User user) {
        send(UserEvent.deleted(user));
    }
    public void sendUpdated(User user) {
        send(UserEvent.updated(user));
    }

    private void send(UserEvent event) {
        try {
            // .get() делаем синхронную отправку: если Kafka недоступна,
            // метод бросит исключение, и транзакция БД сможет откатиться.
            kafkaTemplate.send(TOPIC, event.email(), event)
                    .get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send user event to Kafka", e);
        }
    }
}