package com.plantswap.listings.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ListingSummaryDto(
        UUID id,
        UUID ownerId,
        String type,
        String title,
        BigDecimal priceAmount,
        String priceCurrency,
        String city,
        String status,
        String firstPhotoUrl,
        Instant createdAt
) {}
