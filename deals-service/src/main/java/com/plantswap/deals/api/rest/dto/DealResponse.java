package com.plantswap.deals.api.rest.dto;

import java.time.Instant;
import java.util.UUID;

public record DealResponse(
        UUID id,
        UUID listingId,
        UUID ownerId,
        UUID requesterId,
        String status,
        String note,
        Instant createdAt,
        Instant updatedAt
) {}
