package org.service.service;

import org.events.UserEventType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.service.model.OutboxEvent;
import org.service.model.User;
import org.service.repository.OutboxRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @Mock
    private OutboxRepository outboxRepository;
    @InjectMocks
    private OutboxService outboxService;

    @ParameterizedTest
    @EnumSource(UserEventType.class)
    public void saveUserEventTest(UserEventType type) {
        User user = new User("Ivan", "ivan@test.ru", 30);
        user.setId(1L);

        outboxService.saveUserEvent(user, type);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxRepository).save(captor.capture());

        OutboxEvent saved = captor.getValue();
        assertThat(saved.getAggregateType()).isEqualTo("User");
        assertThat(saved.getAggregateId()).isEqualTo("ivan@test.ru");
        assertThat(saved.getEventType()).isEqualTo(type.name());
        assertThat(saved.getPayload()).contains("id\":1,\"email\":\"ivan@test.ru\",\"type\":\"" + type.name());
    }
}