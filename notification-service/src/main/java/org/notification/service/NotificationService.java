package org.notification.service;

public interface NotificationService {

    void sendCreated(String email);

    void sendUpdated(String email);

    void sendDeleted(String email);
}
