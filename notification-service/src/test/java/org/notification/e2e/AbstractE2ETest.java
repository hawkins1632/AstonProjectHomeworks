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
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Testcontainers
@SpringBootTest
public abstract class AbstractE2ETest extends AbstractGreenMailTest {

    @Container
    static final ConfluentKafkaContainer KAFKA =
            new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    protected static final String TOPIC = "user-events";

    protected Producer<String, UserEvent> kafkaProducer;

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




/*
package org.notification.e2e;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Testcontainers
@SpringBootTest
public abstract class AbstractE2ETest {
    @Container
    static final ConfluentKafkaContainer KAFKA =
            new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    protected static final String TOPIC = "user-events";
    protected static final String RECIPIENT = "to@test.local";
    protected static final String MAIL_FROM = "no-reply@localhost";

    static final int SMTP_PORT = 3025;
    static final String SMTP_USER = "test-user";
    static final String SMTP_PASSWORD = "test-password";

    static GreenMail greenMail;

    static {
        ServerSetup smtpSetup = new ServerSetup(SMTP_PORT, null, "smtp");
        greenMail = new GreenMail(smtpSetup);
        greenMail.setUser(RECIPIENT, SMTP_USER, SMTP_PASSWORD);
        greenMail.start();
        KAFKA.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            greenMail.stop();
            KAFKA.stop();
        }));
    }

    protected Producer<String, UserEvent> kafkaProducer;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.mail.port", () -> SMTP_PORT);
    }

    @BeforeEach
    void setUpProducer() throws FolderException {
        JacksonJsonSerializer<UserEvent> valueSerializer = new JacksonJsonSerializer<>();
        valueSerializer.setAddTypeInfo(false);

        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());

        kafkaProducer = new KafkaProducer<>(
                producerProps,
                new StringSerializer(),
                valueSerializer
        );

        greenMail.purgeEmailFromAllMailboxes();
    }

    @AfterEach
    void tearDownProducer() {
        if (kafkaProducer != null) {
            kafkaProducer.close(Duration.ofSeconds(5));
        }
    }
}*/
