package com.plantswap.deals.domain.model;

import com.plantswap.deals.domain.event.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Агрегат Deal — корневая сущность контекста Deals.
 *
 * Конечный автомат:
 *   PENDING ──► ACCEPTED   (accept,  только owner)
 *   PENDING ──► REJECTED   (reject,  только owner)
 *   PENDING ──► CANCELLED  (cancel,  любой участник)
 *   ACCEPTED ──► COMPLETED (complete, любой участник)
 *   ACCEPTED ──► CANCELLED (cancel,  любой участник)
 */
public class Deal extends AggregateRoot {

    private final DealId id;
    private final ListingId listingId;
    private final OwnerId ownerId;
    private final RequesterId requesterId;
    private final DealNote note;
    private DealStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Deal(DealId id, ListingId listingId, OwnerId ownerId,
                 RequesterId requesterId, DealNote note,
                 DealStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.listingId = listingId;
        this.ownerId = ownerId;
        this.requesterId = requesterId;
        this.note = note;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ── Фабричные методы ──────────────────────────────────────────────────

    public static Deal create(ListingId listingId, OwnerId ownerId,
                               RequesterId requesterId, DealNote note) {
        if (ownerId.value().equals(requesterId.value()))
            throw new IllegalArgumentException(
                    "Нельзя инициировать сделку с самим собой");

        DealId id = DealId.generate();
        Instant now = Instant.now();
        Deal deal = new Deal(id, listingId, ownerId, requesterId,
                note, DealStatus.PENDING, now, now);
        deal.registerEvent(DealCreatedEvent.of(id, listingId, ownerId, requesterId));
        return deal;
    }

    public static Deal reconstitute(DealId id, ListingId listingId, OwnerId ownerId,
                                     RequesterId requesterId, DealNote note,
                                     DealStatus status, Instant createdAt, Instant updatedAt) {
        return new Deal(id, listingId, ownerId, requesterId, note, status, createdAt, updatedAt);
    }

    // ── Команды ───────────────────────────────────────────────────────────

    public void accept(UUID actorId) {
        requireOwner(actorId, "принять сделку");
        requireStatus(DealStatus.PENDING, "принять");
        this.status = DealStatus.ACCEPTED;
        this.updatedAt = Instant.now();
        registerEvent(DealAcceptedEvent.of(id, listingId, ownerId, requesterId));
    }

    public void reject(UUID actorId) {
        requireOwner(actorId, "отклонить сделку");
        requireStatus(DealStatus.PENDING, "отклонить");
        this.status = DealStatus.REJECTED;
        this.updatedAt = Instant.now();
        registerEvent(DealRejectedEvent.of(id, listingId, ownerId, requesterId));
    }

    public void complete(UUID actorId) {
        requireParticipant(actorId, "завершить сделку");
        requireStatus(DealStatus.ACCEPTED, "завершить");
        this.status = DealStatus.COMPLETED;
        this.updatedAt = Instant.now();
        registerEvent(DealCompletedEvent.of(id, listingId, ownerId, requesterId));
    }

    public void cancel(UUID actorId) {
        requireParticipant(actorId, "отменить сделку");
        if (status != DealStatus.PENDING && status != DealStatus.ACCEPTED)
            throw new InvalidDealTransitionException(id, status, "отменить");
        this.status = DealStatus.CANCELLED;
        this.updatedAt = Instant.now();
        registerEvent(DealCancelledEvent.of(id, listingId, ownerId, requesterId, actorId));
    }

    // ── Запросы ───────────────────────────────────────────────────────────

    public boolean isParticipant(UUID userId) {
        return ownerId.value().equals(userId) || requesterId.value().equals(userId);
    }

    // ── Геттеры ───────────────────────────────────────────────────────────

    public DealId id() { return id; }
    public ListingId listingId() { return listingId; }
    public OwnerId ownerId() { return ownerId; }
    public RequesterId requesterId() { return requesterId; }
    public DealNote note() { return note; }
    public DealStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }

    // ── Вспомогательные методы ────────────────────────────────────────────

    private void requireStatus(DealStatus expected, String action) {
        if (this.status != expected)
            throw new InvalidDealTransitionException(id, status, action);
    }

    private void requireParticipant(UUID actorId, String action) {
        if (!isParticipant(actorId))
            throw new DealAccessDeniedException(id, actorId);
    }

    private void requireOwner(UUID actorId, String action) {
        if (!ownerId.value().equals(actorId))
            throw new DealAccessDeniedException(id, actorId);
    }
}
