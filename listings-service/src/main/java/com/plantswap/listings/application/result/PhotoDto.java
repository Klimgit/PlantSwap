package com.plantswap.listings.application.result;

import java.util.UUID;

public record PhotoDto(
        UUID id,
        String url,
        int sortOrder
) {}
