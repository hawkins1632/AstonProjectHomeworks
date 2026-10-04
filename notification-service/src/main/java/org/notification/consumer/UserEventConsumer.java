package org.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.events.UserEvent;
import org.events.UserEventType;
import org.notification.model.ProcessedEvent;
import org.notification.repository.ProcessedEventRepository;
import org.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka-консьюмер событий пользователя.
 * <p>
 * Слушает топик, указанный в свойстве {@code app.kafka.topic}, и обрабатывает
 * события создания, обновления и удаления пользователя, отправляя
 * соответствующее уведомление на email.
 * <p>
 * Обеспечивает идемпотентность обработки: перед обработкой проверяет,
 * не было ли событие с таким {@code eventId} уже обработано ранее.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final NotificationService notificationService;
    private final ProcessedEventRepository processedEventRepository;

    /**
     * Обрабатывает входящее событие пользователя из Kafka.
     * <p>
     * Если событие с таким {@code eventId} уже было обработано, оно пропускается.
     * В зависимости от типа события ({@link UserEventType}) вызывается
     * соответствующий метод отправки уведомления. После успешной обработки
     * событие сохраняется в таблицу обработанных событий.
     *
     * @param event событие пользователя, полученное из Kafka
     */
    @KafkaListener(topics = "${app.kafka.topic}")
    @Transactional
    public void consume(UserEvent event) {
        String email = event.email();
        UserEventType type = event.type();

        log.info("Получено сообщение из Kafka (eventId={}, email={}, type={})", event.eventId(), email, type);

        if (processedEventRepository.existsByEventId(event.eventId())) {
            log.info("Событие eventId={} уже обработано, пропускаем", event.eventId());
            return;
        }

        switch (type) {
            case CREATED -> notificationService.sendCreated(email);
            case UPDATED -> notificationService.sendUpdated(email);
            case DELETED -> notificationService.sendDeleted(email);
        }

        processedEventRepository.save(new ProcessedEvent(event.eventId()));

        log.info("Обработано сообщение из Kafka (eventId={}, email={}, type={})", event.eventId(), email, type);
    }
}