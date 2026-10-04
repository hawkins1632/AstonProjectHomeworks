package org.notification.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Сущность обработанного события.
 * <p>
 * Используется для обеспечения идемпотентности обработки Kafka-событий:
 * перед обработкой события проверяется, не было ли оно уже зафиксировано
 * в этой таблице. Ограничение уникальности по {@code event_id}
 * защищает от повторной записи.
 */
@Entity
@Table(name = "processed_events",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_processed_events_event_id",
                columnNames = "event_id"))
@NoArgsConstructor
@Getter
@Setter
public class ProcessedEvent {
    /**
     * Уникальный идентификатор записи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор обработанного события.
     */
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    /**
     * Дата и время обработки события.
     */
    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    /**
     * Создаёт новую запись об обработанном событии.
     * <p>
     * Время обработки устанавливается в момент создания объекта.
     *
     * @param eventId идентификатор обработанного события
     */
    public ProcessedEvent(UUID eventId) {
        this.eventId = eventId;
        this.processedAt = Instant.now();
    }
}