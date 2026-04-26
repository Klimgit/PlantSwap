package com.plantswap.deals.application.port.in;

import com.plantswap.deals.application.command.DealActionCommand;
import com.plantswap.deals.application.result.DealDto;

public interface CancelDealUseCase {
    DealDto cancel(DealActionCommand command);
}
