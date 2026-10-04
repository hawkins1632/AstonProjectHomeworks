package org.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.events.UserEvent;
import org.events.UserEventType;
import org.service.model.OutboxEvent;
import org.service.model.User;
import org.service.repository.OutboxRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Сервис для сохранения событий пользователя в outbox-таблицу.
 * <p>
 * Реализует часть паттерна Transactional Outbox: сериализует событие
 * {@link UserEvent} в JSON и сохраняет его в БД в рамках текущей
 * транзакции. Публикация события во внешний брокер выполняется отдельно.
 */
@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Сохраняет событие пользователя в outbox-таблицу.
     * <p>
     * Формирует {@link UserEvent} на основе переданного пользователя и типа события,
     * сериализует его в JSON и сохраняет запись {@link OutboxEvent}.
     *
     * @param user пользователь, с которым связано событие
     * @param type тип события (например, {@code CREATED}, {@code UPDATED}, {@code DELETED})
     * @throws IllegalStateException если не удалось сериализовать событие в JSON
     */
    public void saveUserEvent(User user, UserEventType type) {
        UserEvent event = new UserEvent(
                UUID.randomUUID(),
                user.getId(),
                user.getEmail(),
                type
        );
        try {
            OutboxEvent outbox = new OutboxEvent();
            outbox.setAggregateType("User");
            outbox.setAggregateId(user.getEmail());
            outbox.setEventType(type.name());
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outboxRepository.save(outbox);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось сериализовать UserEvent", e);
        }
    }
}
