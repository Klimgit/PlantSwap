package com.plantswap.deals.domain.model;

import java.util.UUID;

public class DealAccessDeniedException extends RuntimeException {

    public DealAccessDeniedException(DealId dealId, UUID userId) {
        super("Пользователь %s не имеет доступа к сделке %s".formatted(userId, dealId));
    }
}
