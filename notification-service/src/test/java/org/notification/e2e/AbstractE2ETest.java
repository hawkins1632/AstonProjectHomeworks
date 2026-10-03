package org.notification.e2e;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.events.UserEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Testcontainers
@SpringBootTest
public abstract class AbstractE2ETest extends AbstractGreenMailTest {

    static final ConfluentKafkaContainer KAFKA =
            new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17");

    protected static final String TOPIC = "user-events";

    protected Producer<String, UserEvent> kafkaProducer;

    static {
        POSTGRES.start();
        KAFKA.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            KAFKA.stop();
            POSTGRES.stop();
        }));
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.kafka.consumer.group-id",
                () -> "notification-e2e-" + System.nanoTime());
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> SMTP_PORT);
        registry.add("spring.mail.username", () -> "");
        registry.add("spring.mail.password", () -> "");
        registry.add("app.mail.from", () -> MAIL_FROM);

        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeEach
    void setUpProducer() {
        JacksonJsonSerializer<UserEvent> valueSerializer = new JacksonJsonSerializer<>();
        valueSerializer.setAddTypeInfo(false);

        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());

        kafkaProducer = new KafkaProducer<>(
                producerProps,
                new StringSerializer(),
                valueSerializer
        );
    }

    @AfterEach
    void tearDownProducer() {
        if (kafkaProducer != null) {
            kafkaProducer.close(Duration.ofSeconds(5));
        }
    }
}