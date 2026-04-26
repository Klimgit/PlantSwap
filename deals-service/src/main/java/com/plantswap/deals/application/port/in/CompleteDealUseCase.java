package com.plantswap.deals.application.port.in;

import com.plantswap.deals.application.command.DealActionCommand;
import com.plantswap.deals.application.result.DealDto;

public interface CompleteDealUseCase {
    DealDto complete(DealActionCommand command);
}
