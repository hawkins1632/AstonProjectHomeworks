package org.events;

import java.util.UUID;

public record UserEvent(
        UUID eventId,
        Long id,
        String email,
        UserEventType type
) {
}
