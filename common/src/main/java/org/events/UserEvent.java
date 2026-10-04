package org.events;

import java.util.UUID;

/**
 * Событие пользователя, передаваемое между микросервисами через Kafka.
 * <p>
 *
 * @param eventId уникальный идентификатор события (используется для идемпотентной обработки)
 * @param id      идентификатор пользователя в БД
 * @param email   электронная почта пользователя
 * @param type    тип события
 */
public record UserEvent(
        UUID eventId,
        Long id,
        String email,
        UserEventType type
) {
}
