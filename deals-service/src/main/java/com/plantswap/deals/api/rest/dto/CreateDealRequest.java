package com.plantswap.deals.api.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateDealRequest(
        @NotNull UUID listingId,
        @NotNull UUID ownerId,
        String note
) {}
