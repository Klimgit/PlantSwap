package com.plantswap.listings.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateListingCommand(
        UUID ownerId,
        String type,
        String title,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        String city
) {}
