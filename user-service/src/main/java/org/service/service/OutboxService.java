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

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
