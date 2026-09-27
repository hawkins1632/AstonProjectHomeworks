package org.service.kafka;

import org.events.UserEvent;
import org.events.UserEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.service.model.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventProducerTest {

    @Mock
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @InjectMocks
    private UserEventProducer producer;

    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(producer, "TOPIC", "user-events");
        user = new User("Ivan", "ivan@test.com", 30);
        user.setId(1L);
    }

    @Test
    void sendCreated_shouldSendEventWithTypeCreated() {
        when(kafkaTemplate.send(eq("user-events"), eq("ivan@test.com"), any(UserEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        producer.sendCreated(user);

        ArgumentCaptor<UserEvent> captor = ArgumentCaptor.forClass(UserEvent.class);
        verify(kafkaTemplate).send(eq("user-events"), eq("ivan@test.com"), captor.capture());

        UserEvent event = captor.getValue();
        assertThat(event.id()).isEqualTo(1L);
        assertThat(event.email()).isEqualTo("ivan@test.com");
        assertThat(event.type()).isEqualTo(UserEventType.CREATED);
    }

    @Test
    void sendDeleted_shouldSendEventWithTypeDeleted() {
        when(kafkaTemplate.send(anyString(), anyString(), any(UserEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        producer.sendDeleted(user);

        ArgumentCaptor<UserEvent> captor = ArgumentCaptor.forClass(UserEvent.class);
        verify(kafkaTemplate).send(eq("user-events"), eq("ivan@test.com"), captor.capture());
        assertThat(captor.getValue().type()).isEqualTo(UserEventType.DELETED);
    }

    @Test
    void sendUpdated_shouldSendEventWithTypeUpdated() {
        when(kafkaTemplate.send(anyString(), anyString(), any(UserEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        producer.sendUpdated(user);

        ArgumentCaptor<UserEvent> captor = ArgumentCaptor.forClass(UserEvent.class);
        verify(kafkaTemplate).send(eq("user-events"), eq("ivan@test.com"), captor.capture());
        assertThat(captor.getValue().type()).isEqualTo(UserEventType.UPDATED);
    }

    @Test
    void send_shouldWrapException_whenKafkaFails() {
        CompletableFuture<SendResult<String, UserEvent>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("kafka down"));
        when(kafkaTemplate.send(anyString(), anyString(), any(UserEvent.class))).thenReturn(failed);

        assertThatThrownBy(() -> producer.sendCreated(user))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to send user event to Kafka");
    }
}