package com.plantswap.listings.domain.repository;

import com.plantswap.listings.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Порт репозитория объявлений
 */
public interface ListingRepository {

    void save(Listing listing);

    Optional<Listing> findById(ListingId id);

    List<Listing> findAll(ListingFilter filter, int page, int size);

    long countAll(ListingFilter filter);

    List<Listing> findByOwnerId(OwnerId ownerId, int page, int size);

    void delete(ListingId id);
}
