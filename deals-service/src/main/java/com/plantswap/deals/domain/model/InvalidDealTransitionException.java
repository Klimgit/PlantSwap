package com.plantswap.deals.domain.model;

public class InvalidDealTransitionException extends RuntimeException {

    public InvalidDealTransitionException(DealId dealId, DealStatus from, String action) {
        super("Невозможно выполнить действие '%s' для сделки %s в статусе %s"
                .formatted(action, dealId, from));
    }
}
