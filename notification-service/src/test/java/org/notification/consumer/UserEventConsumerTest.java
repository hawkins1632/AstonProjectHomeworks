package org.notification.consumer;

import org.events.UserEvent;
import org.events.UserEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.notification.repository.ProcessedEventRepository;
import org.notification.service.NotificationServiceImpl;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventConsumerTest {

    @Mock
    private NotificationServiceImpl notificationService;
    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private UserEventConsumer consumer;

    @Test
    void consume_shouldSendEmailAndMarkProcessed_whenEventIsNew() {
        UUID eventId = UUID.randomUUID();
        UserEvent e = new UserEvent(eventId, 1L, "user@example.com", UserEventType.CREATED);
        when(processedEventRepository.existsByEventId(eventId)).thenReturn(false);

        consumer.consume(e);

        verify(notificationService).sendCreated("user@example.com");
        verify(processedEventRepository).save(argThat(p -> p.getEventId().equals(eventId)));
    }

    @Test
    void consume_shouldSkip_whenEventAlreadyProcessed() {
        UUID eventId = UUID.randomUUID();
        UserEvent e = new UserEvent(eventId, 1L, "user@example.com", UserEventType.CREATED);
        when(processedEventRepository.existsByEventId(eventId)).thenReturn(true);

        consumer.consume(e);

        verifyNoInteractions(notificationService);
        verify(processedEventRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(UserEventType.class)
    void consume_shouldRouteToCorrectMethod_byType(UserEventType type) {
        UUID eventId = UUID.randomUUID();
        UserEvent e = new UserEvent(eventId, 1L, "user@example.com", type);
        when(processedEventRepository.existsByEventId(eventId)).thenReturn(false);

        consumer.consume(e);

        switch (type) {
            case CREATED -> verify(notificationService).sendCreated("user@example.com");
            case UPDATED -> verify(notificationService).sendUpdated("user@example.com");
            case DELETED -> verify(notificationService).sendDeleted("user@example.com");
        }
    }
}