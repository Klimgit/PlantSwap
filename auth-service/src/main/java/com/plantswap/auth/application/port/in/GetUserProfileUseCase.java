package com.plantswap.auth.application.port.in;

import com.plantswap.auth.application.result.UserProfile;

import java.util.UUID;

public interface GetUserProfileUseCase {
    UserProfile getProfile(UUID userId);
}
