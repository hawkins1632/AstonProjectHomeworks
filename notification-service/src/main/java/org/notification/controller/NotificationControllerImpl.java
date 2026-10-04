package org.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.notification.dto.NotificationRequestDto;
import org.notification.dto.NotificationTypeEnum;
import org.notification.service.NotificationServiceImpl;
import org.springframework.web.bind.annotation.RestController;

/**
 * Реализация REST-контроллера для отправки уведомлений.
 * <p>
 * Принимает HTTP-запросы и делегирует отправку уведомлений
 * сервису {@link NotificationServiceImpl}.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class NotificationControllerImpl implements NotificationControllerApi {

    private final NotificationServiceImpl notificationService;

    /**
     * Обрабатывает запрос на отправку email-уведомления.
     * <p>
     * В зависимости от типа уведомления ({@link NotificationTypeEnum})
     * вызывает соответствующий метод сервиса.
     *
     * @param request DTO с email получателя и типом уведомления
     */
    @Override
    public void postEmail(NotificationRequestDto request) {
        String email = request.getEmail();
        NotificationTypeEnum type = request.getType();
        log.info("Получен запрос (email = {}, type = {})", email, type);
        switch (type) {
            case CREATED -> notificationService.sendCreated(email);
            case UPDATED -> notificationService.sendUpdated(email);
            case DELETED -> notificationService.sendDeleted(email);
        }
        log.info("Обработан запрос (email = {}, type = {})", email, type);
    }
}
