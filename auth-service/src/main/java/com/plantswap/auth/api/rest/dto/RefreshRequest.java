package com.plantswap.auth.api.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(

        @NotBlank(message = "Refresh-токен обязателен")
        String refreshToken
) {}
