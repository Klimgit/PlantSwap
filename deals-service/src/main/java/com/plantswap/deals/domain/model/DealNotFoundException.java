package com.plantswap.deals.domain.model;

public class DealNotFoundException extends RuntimeException {

    public DealNotFoundException(String message) {
        super(message);
    }

    public static DealNotFoundException byId(DealId id) {
        return new DealNotFoundException("Сделка не найдена: " + id);
    }
}
