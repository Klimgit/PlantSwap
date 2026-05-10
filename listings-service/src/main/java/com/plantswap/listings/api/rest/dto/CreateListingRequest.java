package com.plantswap.listings.api.rest.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateListingRequest(
        @NotBlank String type,
        @NotBlank String title,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        String city
) {}
