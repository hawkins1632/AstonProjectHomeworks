package org.notification.e2e;

import jakarta.mail.internet.MimeMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.events.UserEvent;
import org.events.UserEventType;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserEventConsumerE2ETest extends AbstractE2ETest {

    @Test
    void shouldSendCreatedEmail() {
        sendEvent(new UserEvent(UUID.randomUUID(), 1L, RECIPIENT, UserEventType.CREATED));

        MimeMessage message = waitForMessage();
        assertThat(message)
                .isNotNull();
        assertMail(message,
                "Аккаунт успешно создан",
                "Ваш аккаунт на сайте был успешно создан");
    }

    @Test
    void shouldSendUpdatedEmail() {
        sendEvent(new UserEvent(UUID.randomUUID(), 2L, RECIPIENT, UserEventType.UPDATED));

        MimeMessage message = waitForMessage();
        assertThat(message).isNotNull();
        assertMail(message,
                "Аккаунт успешно изменён",
                "Ваш аккаунт был изменён");
    }

    @Test
    void shouldSendDeletedEmail() {
        sendEvent(new UserEvent(UUID.randomUUID(), 3L, RECIPIENT, UserEventType.DELETED));

        MimeMessage message = waitForMessage();
        assertThat(message).isNotNull();
        assertMail(message,
                "Аккаунт удалён",
                "Ваш аккаунт был удалён");
    }

    private void sendEvent(UserEvent event) {
        try {
            kafkaProducer.send(new ProducerRecord<>(TOPIC, event)).get();
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось отправить событие в Kafka", e);
        }
    }

    private MimeMessage waitForMessage() {
        try {
            Awaitility.await()
                    .atMost(Duration.ofSeconds(15))
                    .pollInterval(Duration.ofMillis(200))
                    .until(() -> greenMail.getReceivedMessages().length > 0);
            return greenMail.getReceivedMessages()[0];
        } catch (Exception e) {
            return null;
        }
    }

    private void assertMail(MimeMessage message, String expectedSubject, String expectedBodyPart) {
        try {
            assertThat(message.getSubject()).isEqualTo(expectedSubject);
            assertThat(message.getAllRecipients()[0].toString()).isEqualTo(RECIPIENT);
            assertThat(message.getFrom()[0].toString()).isEqualTo(MAIL_FROM);

            String body = (String) message.getContent();
            assertThat(body).contains(expectedBodyPart);
        } catch (Exception e) {
            throw new AssertionError("Ошибка проверки письма", e);
        }
    }
}
