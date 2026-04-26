package com.plantswap.deals.domain.model;

/**
 * Жизненный цикл сделки.
 *
 * Допустимые переходы:
 *
 *   PENDING --> ACCEPTED   (action: owner accepts)
 *   PENDING --> REJECTED   (action: owner rejects)
 *   PENDING --> CANCELLED  (action: любой участник)
 *   ACCEPTED --> COMPLETED (action: любой участник)
 *   ACCEPTED --> CANCELLED (action: любой участник)
 *
 * REJECTED, COMPLETED, CANCELLED — терминальные состояния.
 */
public enum DealStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    COMPLETED,
    CANCELLED;

    public boolean isTerminal() {
        return this == REJECTED || this == COMPLETED || this == CANCELLED;
    }
}
