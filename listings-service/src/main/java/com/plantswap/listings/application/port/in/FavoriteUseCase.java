package com.plantswap.listings.application.port.in;

import com.plantswap.listings.application.result.ListingSummaryDto;
import com.plantswap.listings.application.result.PageDto;

import java.util.UUID;

public interface FavoriteUseCase {
    void addToFavorites(UUID userId, UUID listingId);
    void removeFromFavorites(UUID userId, UUID listingId);
    PageDto<ListingSummaryDto> getFavorites(UUID userId, int page, int size);
}
