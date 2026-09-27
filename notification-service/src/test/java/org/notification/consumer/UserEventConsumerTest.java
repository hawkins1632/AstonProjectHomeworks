package org.notification.consumer;

import org.events.UserEvent;
import org.events.UserEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.notification.service.NotificationServiceImpl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class UserEventConsumerTest {

    @Mock
    private NotificationServiceImpl notificationService;

    @InjectMocks
    private UserEventConsumer consumer;

    @Test
    void consume_created_shouldCallSendCreated() {
        consumer.consume(new UserEvent(1L, "user@example.com", UserEventType.CREATED));
        verify(notificationService).sendCreated("user@example.com");
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void consume_updated_shouldCallSendUpdated() {
        consumer.consume(new UserEvent(1L, "user@example.com", UserEventType.UPDATED));
        verify(notificationService).sendUpdated("user@example.com");
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void consume_deleted_shouldCallSendDeleted() {
        consumer.consume(new UserEvent(1L, "user@example.com", UserEventType.DELETED));
        verify(notificationService).sendDeleted("user@example.com");
        verifyNoMoreInteractions(notificationService);
    }

}