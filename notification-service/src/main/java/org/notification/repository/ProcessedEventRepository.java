package org.notification.repository;

import org.notification.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для управления сущностью {@link ProcessedEvent}.
 * <p>
 * Предоставляет стандартные CRUD-операции и метод проверки
 * наличия обработанного события по его идентификатору.
 */
@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {
    /**
     * Проверяет, было ли событие с указанным идентификатором уже обработано.
     *
     * @param eventId идентификатор события
     * @return {@code true}, если событие уже обработано, иначе {@code false}
     */
    boolean existsByEventId(UUID eventId);
}
