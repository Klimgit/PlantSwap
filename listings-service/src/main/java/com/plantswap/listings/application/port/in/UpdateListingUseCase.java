package com.plantswap.listings.application.port.in;

import com.plantswap.listings.application.command.UpdateListingCommand;
import com.plantswap.listings.application.result.ListingDto;

public interface UpdateListingUseCase {
    ListingDto update(UpdateListingCommand command);
}
