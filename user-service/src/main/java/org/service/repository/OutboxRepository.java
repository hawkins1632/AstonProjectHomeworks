package org.service.repository;

import org.service.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для управления сущностью {@link OutboxEvent}.
 * <p>
 * Предоставляет стандартные CRUD-операции для работы с событиями outbox.
 */
@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
}
