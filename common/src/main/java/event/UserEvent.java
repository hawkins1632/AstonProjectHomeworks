package event;

public record UserEvent(
        Long id,
        String email,
        EventType type
) {
}
