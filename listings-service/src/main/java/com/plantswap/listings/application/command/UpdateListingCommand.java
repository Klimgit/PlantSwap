package com.plantswap.listings.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateListingCommand(
        UUID listingId,
        UUID requesterId,
        String type,
        String title,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        String city
) {}
