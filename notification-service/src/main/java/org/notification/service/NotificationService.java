package org.notification.service;

/**
 * Сервис для отправки email-уведомлений пользователям.
 * <p>
 * Определяет операции отправки уведомлений в зависимости от типа
 * произошедшего события: создание, обновление или удаление аккаунта.
 */
public interface NotificationService {
    /**
     * Отправляет уведомление об успешном создании аккаунта.
     *
     * @param email адрес получателя
     */
    void sendCreated(String email);

    /**
     * Отправляет уведомление об изменении аккаунта.
     *
     * @param email адрес получателя
     */
    void sendUpdated(String email);

    /**
     * Отправляет уведомление об удалении аккаунта.
     *
     * @param email адрес получателя
     */
    void sendDeleted(String email);
}
