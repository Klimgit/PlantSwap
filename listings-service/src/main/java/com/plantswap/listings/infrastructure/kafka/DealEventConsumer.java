package com.plantswap.listings.infrastructure.kafka;

import com.plantswap.listings.application.service.ListingService;
import com.plantswap.listings.infrastructure.persistence.ProcessedEventRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Kafka consumer для событий от deals-service.
 */
@Component
public class DealEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DealEventConsumer.class);
    private static final String DEAL_COMPLETED = "DEAL_COMPLETED";

    private final ListingService listingService;
    private final ProcessedEventRepository processedEventRepository;

    public DealEventConsumer(ListingService listingService,
                              ProcessedEventRepository processedEventRepository) {
        this.listingService = listingService;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(topics = "${kafka.topics.deal-events}",
                   groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handle(ConsumerRecord<String, DealEventMessage> record) {
        DealEventMessage event = record.value();
        if (event == null || event.eventId() == null) return;

        if (processedEventRepository.existsById(event.eventId())) {
            log.debug("Событие уже обработано, пропускаем: eventId={}", event.eventId());
            return;
        }

        try {
            if (DEAL_COMPLETED.equals(event.eventType())) {
                String listingId = event.payload().get("listingId");
                if (listingId != null) {
                    listingService.closeByDeal(UUID.fromString(listingId));
                }
            }
            processedEventRepository.markProcessed(event.eventId());
        } catch (Exception e) {
            log.error("Ошибка обработки события deal: eventId={}", event.eventId(), e);
            throw e;
        }
    }
}
