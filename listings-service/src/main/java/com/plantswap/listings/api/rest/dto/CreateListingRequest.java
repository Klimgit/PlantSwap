package com.plantswap.listings.api.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateListingRequest(
        @NotBlank(message = "Тип объявления обязателен") String type,
        @NotBlank(message = "Заголовок обязателен")
        @Size(min = 3, max = 120, message = "Заголовок: от 3 до 120 символов")
        String title,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        String city
) {}
