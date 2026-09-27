package org.service.e2e;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.events.UserEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Testcontainers
public abstract class AbstractE2ETest {
    protected static final String TOPIC = "user-events-e2e";

    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17");

    static final ConfluentKafkaContainer KAFKA =
            new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    static {
        POSTGRES.start();
        KAFKA.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            POSTGRES.stop();
            KAFKA.stop();
        }));
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("app.kafka.topic", () -> TOPIC);
    }

    @Autowired
    protected RestTestClient restTestClient;

    @Autowired
    protected UserRepository userRepository;

    private Consumer<String, UserEvent> kafkaConsumer;

    @BeforeEach
    void setUpKafkaConsumer() {
        JacksonJsonDeserializer<UserEvent> valueDeserializer =
                new JacksonJsonDeserializer<>(UserEvent.class, false);
        valueDeserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", KAFKA.getBootstrapServers());
        props.put("group.id", "e2e-consumer-" + System.nanoTime());
        props.put("auto.offset.reset", "earliest");

        kafkaConsumer = new DefaultKafkaConsumerFactory<String, UserEvent>(
                props,
                new StringDeserializer(),
                valueDeserializer
        ).createConsumer();

        kafkaConsumer.subscribe(List.of(TOPIC));
        kafkaConsumer.poll(Duration.ofMillis(500));
    }

    @AfterEach
    void cleanup() {
        if (kafkaConsumer != null) kafkaConsumer.close();
        userRepository.deleteAll();
    }

    protected UserEvent awaitEvent(String email, org.events.UserEventType type) {
        UserEvent[] holder = new UserEvent[1];
        await().atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(300))
                .until(() -> {
                    ConsumerRecords<String, UserEvent> records =
                            kafkaConsumer.poll(Duration.ofMillis(500));
                    for (ConsumerRecord<String, UserEvent> record : records) {
                        UserEvent value = record.value();
                        if (value != null
                                && email.equals(value.email())
                                && type == value.type()) {
                            holder[0] = value;
                            return true;
                        }
                    }
                    return false;
                });
        return holder[0];
    }
}