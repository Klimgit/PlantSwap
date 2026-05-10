package com.plantswap.auth.infrastructure.kafka;

import com.plantswap.auth.application.port.out.EventPublisherPort;
import com.plantswap.auth.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Адаптер публикации доменных событий в Kafka.
 */
@Component
public class KafkaEventPublisher implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private static final int SEND_TIMEOUT_SEC = 30;

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
        try {
            kafkaTemplate.send(authEventsTopic, event.eventId().toString(), event)
                    .get(SEND_TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Публикация в Kafka прервана", e);
        } catch (ExecutionException e) {
            Throwable c = e.getCause() != null ? e.getCause() : e;
            log.error("Не удалось отправить событие {} в Kafka", event.eventType(), c);
            if (c instanceof RuntimeException re) {
                throw re;
            }
            throw new IllegalStateException("Не удалось отправить событие в Kafka", c);
        } catch (TimeoutException e) {
            throw new IllegalStateException("Таймаут отправки события в Kafka", e);
        }
    }
}
