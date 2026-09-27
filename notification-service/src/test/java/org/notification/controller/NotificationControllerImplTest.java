package org.notification.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.notification.dto.NotificationRequestDto;
import org.notification.dto.NotificationTypeEnum;
import org.notification.service.NotificationServiceImpl;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerImplTest {

    @Mock
    private NotificationServiceImpl notificationService;

    @InjectMocks
    private NotificationControllerImpl controller;

    private NotificationRequestDto dto(String email, NotificationTypeEnum type) {
        NotificationRequestDto dto = new NotificationRequestDto();
        dto.setEmail(email);
        dto.setType(type);
        return dto;
    }

    @Test
    void postEmail_created_shouldDelegateToService() {
        controller.postEmail(dto("user@example.com", NotificationTypeEnum.CREATED));
        verify(notificationService).sendCreated("user@example.com");
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void postEmail_updated_shouldDelegateToService() {
        controller.postEmail(dto("user@example.com", NotificationTypeEnum.UPDATED));
        verify(notificationService).sendUpdated("user@example.com");
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void postEmail_deleted_shouldDelegateToService() {
        controller.postEmail(dto("user@example.com", NotificationTypeEnum.DELETED));
        verify(notificationService).sendDeleted("user@example.com");
        verifyNoMoreInteractions(notificationService);
    }
}