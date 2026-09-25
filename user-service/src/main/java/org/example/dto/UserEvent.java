package org.example.dto;

import org.example.model.User;

import java.time.Instant;

public record UserEvent(
        Long id,
        String email,
        Operation operation,
        Instant occurredAt
) {
    public enum Operation {
        CREATED,
        UPDATED,
        DELETED
    }

    public static UserEvent updated(User user) {
        return new UserEvent(user.getId(), user.getEmail(), Operation.UPDATED, Instant.now());
    }

    public static UserEvent created(User user) {
        return new UserEvent(user.getId(), user.getEmail(), Operation.CREATED, Instant.now());
    }

    public static UserEvent deleted(User user) {
        return new UserEvent(user.getId(), user.getEmail(), Operation.DELETED, Instant.now());
    }
}
