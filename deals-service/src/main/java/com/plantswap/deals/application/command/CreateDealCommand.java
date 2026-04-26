package com.plantswap.deals.application.command;

import java.util.UUID;

public record CreateDealCommand(
        UUID listingId,
        UUID ownerId,
        UUID requesterId,
        String note
) {}
