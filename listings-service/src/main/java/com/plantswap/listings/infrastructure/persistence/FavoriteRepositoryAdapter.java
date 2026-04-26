package com.plantswap.listings.infrastructure.persistence;

import com.plantswap.listings.domain.model.Favorite;
import com.plantswap.listings.domain.model.ListingId;
import com.plantswap.listings.domain.repository.FavoriteRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/** Адаптер репозитория избранных объявлений. */
@Repository
public class FavoriteRepositoryAdapter implements FavoriteRepository {

    private final SpringDataFavoriteRepository jpa;

    public FavoriteRepositoryAdapter(SpringDataFavoriteRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Favorite favorite) {
        jpa.save(new FavoriteJpaEntity(
                favorite.userId(), favorite.listingId().value(), favorite.createdAt()));
    }

    @Override
    public void delete(UUID userId, ListingId listingId) {
        jpa.deleteByUserIdAndListingId(userId, listingId.value());
    }

    @Override
    public boolean exists(UUID userId, ListingId listingId) {
        return jpa.existsByUserIdAndListingId(userId, listingId.value());
    }

    @Override
    public List<ListingId> findListingIdsByUserId(UUID userId, int page, int size) {
        return jpa.findListingIdsByUserId(userId, PageRequest.of(page, size))
                .stream().map(ListingId::of).toList();
    }
}
