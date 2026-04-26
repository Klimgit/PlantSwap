package com.plantswap.listings.application.service;

import com.plantswap.listings.application.port.in.FavoriteUseCase;
import com.plantswap.listings.application.result.ListingSummaryDto;
import com.plantswap.listings.application.result.PageDto;
import com.plantswap.listings.domain.model.Favorite;
import com.plantswap.listings.domain.model.Listing;
import com.plantswap.listings.domain.model.ListingId;
import com.plantswap.listings.domain.model.ListingNotFoundException;
import com.plantswap.listings.domain.repository.FavoriteRepository;
import com.plantswap.listings.domain.repository.ListingFilter;
import com.plantswap.listings.domain.repository.ListingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Application-сервис для управления избранными объявлениями. */
@Service
@Transactional
public class FavoriteService implements FavoriteUseCase {

    private static final Logger log = LoggerFactory.getLogger(FavoriteService.class);

    private final FavoriteRepository favoriteRepository;
    private final ListingRepository listingRepository;
    private final ListingService listingService;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           ListingRepository listingRepository,
                           ListingService listingService) {
        this.favoriteRepository = favoriteRepository;
        this.listingRepository = listingRepository;
        this.listingService = listingService;
    }

    @Override
    public void addToFavorites(UUID userId, UUID listingId) {
        ListingId lid = ListingId.of(listingId);
        listingRepository.findById(lid)
                .orElseThrow(() -> ListingNotFoundException.byId(lid));

        if (!favoriteRepository.exists(userId, lid)) {
            favoriteRepository.save(Favorite.of(userId, lid));
            log.debug("Добавлено в избранное: userId={}, listingId={}", userId, listingId);
        }
    }

    @Override
    public void removeFromFavorites(UUID userId, UUID listingId) {
        favoriteRepository.delete(userId, ListingId.of(listingId));
        log.debug("Удалено из избранного: userId={}, listingId={}", userId, listingId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<ListingSummaryDto> getFavorites(UUID userId, int page, int size) {
        List<ListingId> ids = favoriteRepository.findListingIdsByUserId(userId, page, size);
        List<ListingSummaryDto> summaries = ids.stream()
                .flatMap(id -> listingRepository.findById(id).stream())
                .map(listingService::toSummaryPublic)
                .toList();
        return PageDto.of(summaries, page, size, summaries.size());
    }
}
