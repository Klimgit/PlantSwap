package com.plantswap.auth.api.rest.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String username,
        String email,
        String city,
        Instant createdAt
) {}
