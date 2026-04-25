package com.plantswap.auth.application.port.in;

import java.util.UUID;

public interface LogoutUseCase {
    void logout(UUID userId);
}
