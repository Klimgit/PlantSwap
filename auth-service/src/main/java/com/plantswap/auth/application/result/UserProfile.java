package com.plantswap.auth.application.result;

import java.time.Instant;
import java.util.UUID;

public record UserProfile(
        UUID id,
        String username,
        String email,
        String city,
        Instant createdAt
) {}
