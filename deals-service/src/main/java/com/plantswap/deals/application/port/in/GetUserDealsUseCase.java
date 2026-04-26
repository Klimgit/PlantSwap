package com.plantswap.deals.application.port.in;

import com.plantswap.deals.application.result.DealDto;
import com.plantswap.deals.application.result.PageDto;

import java.util.UUID;

public interface GetUserDealsUseCase {
    PageDto<DealDto> getUserDeals(UUID userId, int page, int size);
}
