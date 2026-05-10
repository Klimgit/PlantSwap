package com.plantswap.listings.infrastructure.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper;
    private final ListingService listingService;
    private final ProcessedEventRepository processedEventRepository;

    public DealEventConsumer(ObjectMapper objectMapper,
                             ListingService listingService,
                             ProcessedEventRepository processedEventRepository) {
        this.objectMapper = objectMapper;
        this.listingService = listingService;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(topics = "${kafka.topics.deal-events}",
                   groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handle(ConsumerRecord<String, String> record) throws Exception {
        String raw = record.value();
        if (raw == null || raw.isBlank()) {
            return;
        }

        JsonNode root = objectMapper.readTree(raw);
        if (!root.hasNonNull("eventId")) {
            log.warn("Событие deal без eventId, пропуск");
            return;
        }

        UUID eventId = UUID.fromString(root.get("eventId").asText());

        if (processedEventRepository.existsById(eventId)) {
            log.debug("Событие уже обработано, пропускаем: eventId={}", eventId);
            return;
        }

        String type = root.path("eventType").asText("");
        try {
            if (DEAL_COMPLETED.equals(type)) {
                JsonNode listingNode = root.get("listingId");
                if (listingNode != null && listingNode.hasNonNull("value")) {
                    UUID listingUuid = UUID.fromString(listingNode.get("value").asText());
                    listingService.closeByDeal(listingUuid);
                } else {
                    log.warn("DEAL_COMPLETED без listingId.value: eventId={}", eventId);
                }
            }
            processedEventRepository.markProcessed(eventId);
        } catch (Exception e) {
            log.error("Ошибка обработки события deal: eventId={}", eventId, e);
            throw e;
        }
    }
}
