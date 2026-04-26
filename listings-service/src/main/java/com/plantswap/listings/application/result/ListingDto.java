package com.plantswap.listings.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ListingDto(
        UUID id,
        UUID ownerId,
        String type,
        String title,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        String city,
        String status,
        List<PhotoDto> photos,
        boolean isFavorite,
        Instant createdAt,
        Instant updatedAt
) {}
