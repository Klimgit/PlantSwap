package com.plantswap.deals.application.result;

import java.time.Instant;
import java.util.UUID;

public record DealDto(
        UUID id,
        UUID listingId,
        UUID ownerId,
        UUID requesterId,
        String status,
        String note,
        Instant createdAt,
        Instant updatedAt
) {}
