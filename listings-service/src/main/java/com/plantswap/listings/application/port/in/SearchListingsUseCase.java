package com.plantswap.listings.application.port.in;

import com.plantswap.listings.application.result.ListingSummaryDto;
import com.plantswap.listings.application.result.PageDto;
import com.plantswap.listings.domain.repository.ListingFilter;

public interface SearchListingsUseCase {
    PageDto<ListingSummaryDto> search(ListingFilter filter, int page, int size);
}
