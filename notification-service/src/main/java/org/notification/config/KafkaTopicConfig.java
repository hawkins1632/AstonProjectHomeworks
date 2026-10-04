package org.notification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Конфигурация Kafka-топиков для микросервиса notification-service.
 * <p>
 * Создаёт необходимые топики при старте приложения, если они ещё не существуют.
 */
@Configuration
public class KafkaTopicConfig {

    /**
     * Создаёт Kafka-топик для событий пользователей.
     * <p>
     * Имя топика берётся из свойства {@code app.kafka.topic}. Топик создаётся
     * с одной партицией и одним репликой.
     *
     * @param topic имя топика, полученное из конфигурации
     * @return описание нового топика {@link NewTopic}
     */
    @Bean
    public NewTopic userEventsTopic(@Value("${app.kafka.topic}") String topic) {
        return TopicBuilder.name(topic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
