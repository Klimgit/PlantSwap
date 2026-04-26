package com.plantswap.deals.application.port.in;

import com.plantswap.deals.application.result.DealDto;

import java.util.UUID;

public interface GetDealUseCase {
    DealDto getDeal(UUID dealId, UUID requesterId);
}
