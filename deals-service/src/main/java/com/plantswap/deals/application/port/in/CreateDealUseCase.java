package com.plantswap.deals.application.port.in;

import com.plantswap.deals.application.command.CreateDealCommand;
import com.plantswap.deals.application.result.DealDto;

public interface CreateDealUseCase {
    DealDto create(CreateDealCommand command);
}
