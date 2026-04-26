package com.plantswap.listings.application.port.in;

import java.util.UUID;

public interface DeleteListingUseCase {
    void delete(UUID listingId, UUID requesterId);
}
