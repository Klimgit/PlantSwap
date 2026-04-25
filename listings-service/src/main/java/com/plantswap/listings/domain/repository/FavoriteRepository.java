package com.plantswap.listings.domain.repository;

import com.plantswap.listings.domain.model.Favorite;
import com.plantswap.listings.domain.model.ListingId;

import java.util.List;
import java.util.UUID;

/** Порт репозитория избранных объявлений. */
public interface FavoriteRepository {

    void save(Favorite favorite);

    void delete(UUID userId, ListingId listingId);

    boolean exists(UUID userId, ListingId listingId);

    List<ListingId> findListingIdsByUserId(UUID userId, int page, int size);
}
