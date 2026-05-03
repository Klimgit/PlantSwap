package com.plantswap.chat.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** JPA-сущность таблицы conversations. */
@Entity
@Table(name = "conversations")
public class ConversationJpaEntity {

    @Id
    private UUID id;   // == dealId

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "requester_id", nullable = false)
    private UUID requesterId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ConversationJpaEntity() {}

    public ConversationJpaEntity(UUID id, UUID ownerId, UUID requesterId, Instant createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.requesterId = requesterId;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getOwnerId() { return ownerId; }
    public UUID getRequesterId() { return requesterId; }
    public Instant getCreatedAt() { return createdAt; }
}
