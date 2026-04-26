package com.plantswap.listings.infrastructure.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DealEventMessage(
        String eventType,
        UUID eventId,
        Instant occurredAt,
        Map<String, String> payload
) {}
