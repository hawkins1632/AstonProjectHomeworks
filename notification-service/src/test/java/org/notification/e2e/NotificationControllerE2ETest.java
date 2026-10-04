package org.notification.e2e;

import jakarta.mail.internet.MimeMessage;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class NotificationControllerE2ETest extends AbstractE2ETest {

    @Autowired
    private RestTestClient restTestClient;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> SMTP_PORT);
        registry.add("spring.mail.username", () -> "");
        registry.add("spring.mail.password", () -> "");
        registry.add("app.mail.from", () -> MAIL_FROM);
    }

    @Test
    void shouldSendCreatedEmail() {
        postAndExpectAccepted(Map.of("email", RECIPIENT, "type", "CREATED"));

        MimeMessage message = waitForMessage();
        assertThat(message).isNotNull();
        assertMail(message,
                "Аккаунт успешно создан",
                "Ваш аккаунт на сайте был успешно создан");
    }

    @Test
    void shouldSendUpdatedEmail() {
        postAndExpectAccepted(Map.of("email", RECIPIENT, "type", "UPDATED"));

        MimeMessage message = waitForMessage();
        assertThat(message).isNotNull();
        assertMail(message,
                "Аккаунт успешно изменён",
                "Ваш аккаунт был изменён");
    }

    @Test
    void shouldSendDeletedEmail() {
        postAndExpectAccepted(Map.of("email", RECIPIENT, "type", "DELETED"));

        MimeMessage message = waitForMessage();
        assertThat(message).isNotNull();
        assertMail(message,
                "Аккаунт удалён",
                "Ваш аккаунт был удалён");
    }

    @Test
    void shouldReturnBadRequestForInvalidEmail() {
        restTestClient.post()
                .uri("/api/notification/email")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("email", "not-an-email", "type", "CREATED"))
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(greenMail.getReceivedMessages()).isEmpty();
    }

    private void postAndExpectAccepted(Map<String, String> body) {
        restTestClient.post()
                .uri("/api/notification/email")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody().isEmpty();
    }

    private MimeMessage waitForMessage() {
        try {
            Awaitility.await()
                    .atMost(Duration.ofSeconds(10))
                    .pollInterval(Duration.ofMillis(100))
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

            String body = extractBody(message);
            assertThat(body).contains(expectedBodyPart);
        } catch (Exception e) {
            throw new AssertionError("Ошибка проверки письма", e);
        }
    }

    private String extractBody(MimeMessage message) throws Exception {
        Object content = message.getContent();
        if (content instanceof String s) {
            return s;
        }
        if (content instanceof jakarta.mail.Multipart multipart) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                jakarta.mail.BodyPart part = multipart.getBodyPart(i);
                if (part.getContent() instanceof String s) {
                    sb.append(s);
                }
            }
            return sb.toString();
        }
        return String.valueOf(content);
    }
}