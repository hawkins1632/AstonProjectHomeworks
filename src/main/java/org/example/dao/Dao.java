package org.example.dao;

import java.util.List;
import java.util.Optional;

/**
 * Базовый интерфейс DAO для выполнения CRUD операций с сущностями.
 *
 * @param <T> тип сущности, с которой работает DAO
 */
public interface Dao <T> {
    /**
     * Сохраняет новую сущность в БД.
     *
     * @param object сущность для сохранения
     * @return сохранённая сущность
     */
    T save(T object);

    /**
     * Находит сущность по её идентификатору
     *
     * @param id идентификатор сущности
     * @return {@link Optional} с найденной сущностью или пустой {@link Optional},
     * если сущность не найдена
     */
    Optional<T> findById(Long id);

    /**
     * Возвращает список всех сущностей из базы данных.
     *
     * @return список сущностей
     */
    List<T> findAll();

    /**
     * Обновляет существующую сущность в БД.
     *
     * @param object сущность с обновлёнными данными
     * @return обновлённая сущность
     */
    T update(T object);

    /**
     * Удаляет сущность по её идентификатору
     *
     * @param id идентификатор сущности для удаления
     */
    void delete(Long id);
}
