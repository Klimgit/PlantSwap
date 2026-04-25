package com.plantswap.listings.domain.model;

import java.math.BigDecimal;

public record Price(BigDecimal amount, String currency) {

    private static final int CURRENCY_LENGTH = 3;

    public Price {
        if (amount == null)
            throw new IllegalArgumentException("Сумма цены не может быть null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Цена должна быть больше нуля");
        if (currency == null || currency.isBlank())
            throw new IllegalArgumentException("Валюта не может быть пустой");
        currency = currency.toUpperCase().strip();
        if (currency.length() != CURRENCY_LENGTH)
            throw new IllegalArgumentException("Код валюты должен содержать ровно 3 символа (ISO 4217)");
    }

    public static Price ofRubles(BigDecimal amount) {
        return new Price(amount, "RUB");
    }

    @Override
    public String toString() {
        return "%s %s".formatted(amount.toPlainString(), currency);
    }
}
