package com.plantswap.deals.infrastructure.kafka;

import com.plantswap.deals.application.port.out.EventPublisherPort;
import com.plantswap.deals.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisher implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String dealEventsTopic;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${kafka.topics.deal-events}") String dealEventsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.dealEventsTopic = dealEventsTopic;
    }

    @Override
    public void publish(DomainEvent event) {
        log.debug("Публикую событие {} в топик {}", event.eventType(), dealEventsTopic);
        kafkaTemplate.send(dealEventsTopic, event.eventId().toString(), event);
    }
}
