package com.plantswap.deals.domain.repository;

import com.plantswap.deals.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Порт репозитория сделок. */
public interface DealRepository {

    void save(Deal deal);

    Optional<Deal> findById(DealId id);

    List<Deal> findByUserId(UUID userId, int page, int size);

    long countByUserId(UUID userId);

    List<Deal> findActiveByListingIdAndRequesterId(ListingId listingId, RequesterId requesterId);

    void delete(DealId id);
}
