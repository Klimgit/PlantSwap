package com.plantswap.listings.application.port.in;

import com.plantswap.listings.application.command.CreateListingCommand;
import com.plantswap.listings.application.result.ListingDto;

public interface CreateListingUseCase {
    ListingDto create(CreateListingCommand command);
}
