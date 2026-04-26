package com.plantswap.deals.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "deals")
public class DealJpaEntity {

    @Id
    private UUID id;

    @Column(name = "listing_id", nullable = false)
    private UUID listingId;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "requester_id", nullable = false)
    private UUID requesterId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected DealJpaEntity() {}

    public DealJpaEntity(UUID id, UUID listingId, UUID ownerId, UUID requesterId,
                          String status, String note, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.listingId = listingId;
        this.ownerId = ownerId;
        this.requesterId = requesterId;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getListingId() { return listingId; }
    public UUID getOwnerId() { return ownerId; }
    public UUID getRequesterId() { return requesterId; }
    public String getStatus() { return status; }
    public String getNote() { return note; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setStatus(String status) { this.status = status; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
