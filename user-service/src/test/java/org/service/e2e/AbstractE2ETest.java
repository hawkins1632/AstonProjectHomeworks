package org.service.e2e;

import io.debezium.testing.testcontainers.ConnectorConfiguration;
import io.debezium.testing.testcontainers.DebeziumContainer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.events.UserEvent;
import org.events.UserEventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.service.repository.OutboxRepository;
import org.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Testcontainers
public abstract class AbstractE2ETest {
    protected static final String TOPIC = "user-events-e2e";

    static final Network NETWORK = Network.newNetwork();

    static final KafkaContainer KAFKA =
            new KafkaContainer()
                    .withNetwork(NETWORK)
                    .withNetworkAliases("kafka");

    public static PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
            DockerImageName.parse("debezium/postgres:17-alpine").asCompatibleSubstituteFor("postgres"))
            .withNetwork(NETWORK)
            .withNetworkAliases("postgres");


    public static DebeziumContainer DEBEZIUM = new DebeziumContainer("debezium/connect:2.7.3.Final")
            .withNetwork(NETWORK)
            .withKafka(KAFKA)
            .dependsOn(KAFKA);

    static {
        Startables.deepStart(Stream.of(KAFKA, POSTGRES, DEBEZIUM)).join();

        ConnectorConfiguration connector = ConnectorConfiguration
                .forJdbcContainer(POSTGRES)
                .with("plugin.name", "pgoutput")
                .with("table.include.list", "public.outbox_events")
                .with("topic.prefix", "e2e")
                .with("key.converter", "org.apache.kafka.connect.json.JsonConverter")
                .with("key.converter.schemas.enable", "false")
                .with("value.converter", "org.apache.kafka.connect.json.JsonConverter")
                .with("value.converter.schemas.enable", "false")
                .with("transforms", "outbox")
                .with("transforms.outbox.type", "io.debezium.transforms.outbox.EventRouter")
                .with("transforms.outbox.table.field.event.id", "id")
                .with("transforms.outbox.table.field.event.key", "aggregate_id")
                .with("transforms.outbox.table.field.event.payload", "payload")
                .with("transforms.outbox.table.field.event.type", "event_type")
                .with("transforms.outbox.route.by.field", "aggregate_type")
                .with("transforms.outbox.route.topic.replacement", TOPIC)
                .with("transforms.outbox.table.expand.json.payload", "true");

        DEBEZIUM.registerConnector("user-outbox-connector", connector);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DEBEZIUM.stop();
            KAFKA.stop();
            POSTGRES.stop();
        }));
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("app.kafka.topic", () -> TOPIC);
    }

    @Autowired
    protected RestTestClient restTestClient;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected OutboxRepository outboxRepository;

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
        outboxRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected void awaitEvent(String email, UserEventType type, Long expectedId) {
        await().atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> {
                    ConsumerRecords<String, UserEvent> records =
                            kafkaConsumer.poll(Duration.ofMillis(500));

                    boolean found = false;
                    for (ConsumerRecord<String, UserEvent> record : records) {
                        UserEvent value = record.value();
                        if (value != null
                                && expectedId.equals(value.id())
                                && email.equals(value.email())
                                && type == value.type()) {
                            found = true;
                            break;
                        }
                    }

                    assertThat(found)
                            .as("User event: id=%s, email=%s, type=%s",
                                    expectedId, email, type)
                            .isTrue();
                });
    }
}