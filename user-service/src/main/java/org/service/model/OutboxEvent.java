package org.service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность события outbox для паттерна Transactional Outbox.
 * <p>
 * Используется для надёжной публикации событий в брокер сообщений (Kafka)
 * в рамках той же транзакции, что и изменение бизнес-данных. Запись события
 * в таблицу {@code outbox_events} гарантирует, что событие не будет потеряно
 * даже при временной недоступности брокера.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    /**
     * Уникальный идентификатор события.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Тип агрегата, к которому относится событие.
     */
    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    /**
     * Идентификатор агрегата.
     */
    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    /**
     * Тип события.
     */
    @Column(name = "event_type", nullable = false)
    private String eventType;

    /**
     * Сериализованное в JSON тело события.
     */
    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

    /**
     * Дата и время создания события. Заполняется автоматически перед сохранением.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Устанавливает дату создания перед сохранением события в БД.
     */
    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
