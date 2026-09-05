package org.example.exception;
/**
 * Исключение, выбрасываемое при возникновении ошибок во время взаимодействия
 * с базой данных PostgreSQL или при сбоях в работе фреймворка Hibernate.
 * Служит оберткой для низкоуровневых исключений persistence-слоя.
 */
public class DBException extends RuntimeException{
    /**
     * Создает новое исключение базы данных с детализированным текстовым сообщением.
     * @param message описание причины возникновения ошибки
     */
    public DBException(String message) {
        super(message);
    }
}
