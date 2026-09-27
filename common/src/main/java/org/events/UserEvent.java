package org.events;

public record UserEvent(
        Long id,
        String email,
        UserEventType type
) {
}
