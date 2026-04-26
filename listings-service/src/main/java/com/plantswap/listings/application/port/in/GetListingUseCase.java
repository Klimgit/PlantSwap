package com.plantswap.listings.application.port.in;

import com.plantswap.listings.application.result.ListingDto;

import java.util.UUID;

public interface GetListingUseCase {
    ListingDto getListing(UUID listingId, UUID requesterId);
}
