package com.plantswap.listings.application.port.in;

import com.plantswap.listings.application.result.ListingSummaryDto;
import com.plantswap.listings.application.result.PageDto;

import java.util.UUID;

public interface GetUserListingsUseCase {
    PageDto<ListingSummaryDto> getUserListings(UUID ownerId, int page, int size);
}
