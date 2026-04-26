package com.plantswap.listings.infrastructure.kafka;

import com.plantswap.listings.application.port.out.EventPublisherPort;
import com.plantswap.listings.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/** Адаптер публикации доменных событий listings в Kafka. */
@Component
public class KafkaEventPublisher implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String listingEventsTopic;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${kafka.topics.listing-events}") String listingEventsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.listingEventsTopic = listingEventsTopic;
    }

    @Override
    public void publish(DomainEvent event) {
        log.debug("Публикую событие {} в топик {}", event.eventType(), listingEventsTopic);
        kafkaTemplate.send(listingEventsTopic, event.eventId().toString(), event);
    }
}
