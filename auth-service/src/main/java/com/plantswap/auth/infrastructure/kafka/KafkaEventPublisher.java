package com.plantswap.auth.infrastructure.kafka;

import com.plantswap.auth.application.port.out.EventPublisherPort;
import com.plantswap.auth.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Адаптер публикации доменных событий в Kafka.
 */
@Component
public class KafkaEventPublisher implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String authEventsTopic;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${kafka.topics.auth-events}") String authEventsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.authEventsTopic = authEventsTopic;
    }

    @Override
    public void publish(DomainEvent event) {
        log.debug("Публикую событие {} в топик {}", event.eventType(), authEventsTopic);
        kafkaTemplate.send(authEventsTopic, event.eventId().toString(), event);
    }
}
