package com.plantswap.deals.api.rest.dto;

public record ErrorResponse(
        String code,
        String message
) {}
