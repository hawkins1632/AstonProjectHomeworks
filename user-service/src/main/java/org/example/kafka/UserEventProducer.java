package org.example.kafka;

import event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public void send(UserEvent event) {

        kafkaTemplate.send(topic, event.getEmail(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info(
                                "Kafka event sent: event={}, email={}, topic={}",
                                event.getEvent(),
                                event.getEmail(),
                                topic
                        );
                    } else {
                        log.error(
                                "Failed to send Kafka event: event={}, email={}",
                                event.getEvent(),
                                event.getEmail(),
                                ex
                        );
                    }
                });
    }
}