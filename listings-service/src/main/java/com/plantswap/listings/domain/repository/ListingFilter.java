package com.plantswap.listings.domain.repository;

import com.plantswap.listings.domain.model.ListingStatus;
import com.plantswap.listings.domain.model.ListingType;

import java.math.BigDecimal;

public record ListingFilter(
        String searchQuery,
        ListingType type,
        ListingStatus status,
        String city,
        BigDecimal priceMin,
        BigDecimal priceMax
) {
    public static ListingFilter activeOnly() {
        return new ListingFilter(null, null, ListingStatus.ACTIVE, null, null, null);
    }
}
