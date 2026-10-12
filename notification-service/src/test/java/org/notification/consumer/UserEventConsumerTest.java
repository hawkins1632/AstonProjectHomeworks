package org.notification.consumer;

import org.events.UserEvent;
import org.events.UserEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.notification.model.ProcessedEvent;
import org.notification.repository.ProcessedEventRepository;
import org.notification.service.NotificationServiceImpl;
import org.springframework.dao.DataIntegrityViolationException;

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
    void consume_shouldReserveEventBeforeSendingEmail_whenEventIsNew() {
        UUID eventId = UUID.randomUUID();
        UserEvent e = new UserEvent(eventId, 1L, "user@example.com", UserEventType.CREATED);
        when(processedEventRepository.existsByEventId(eventId)).thenReturn(false);

        consumer.consume(e);

        InOrder inOrder = inOrder(processedEventRepository, notificationService);
        inOrder.verify(processedEventRepository).save(argThat(p -> p.getEventId().equals(eventId)));
        inOrder.verify(notificationService).sendCreated("user@example.com");
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

    @Test
    void consume_shouldSkipSendingEmail_whenEventIsBeingProcessedConcurrently() {
        UUID eventId = UUID.randomUUID();
        UserEvent e = new UserEvent(eventId, 1L, "user@example.com", UserEventType.CREATED);
        when(processedEventRepository.existsByEventId(eventId)).thenReturn(false);
        when(processedEventRepository.save(any(ProcessedEvent.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key violates unique constraint"));

        consumer.consume(e);

        verifyNoInteractions(notificationService);
        verify(processedEventRepository).save(any(ProcessedEvent.class));
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