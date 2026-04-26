package com.plantswap.listings.application.port.in;

import java.util.UUID;

public interface RemovePhotoUseCase {
    void removePhoto(UUID listingId, UUID photoId, UUID requesterId);
}
