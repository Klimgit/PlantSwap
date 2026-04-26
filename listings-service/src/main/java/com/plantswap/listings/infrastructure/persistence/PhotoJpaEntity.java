package com.plantswap.listings.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** JPA-сущность таблицы photos_meta. */
@Entity
@Table(name = "photos_meta")
public class PhotoJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private ListingJpaEntity listing;

    @Column(name = "s3_key", nullable = false, length = 512)
    private String s3Key;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Instant uploadedAt;

    protected PhotoJpaEntity() {}

    public PhotoJpaEntity(UUID id, ListingJpaEntity listing,
                          String s3Key, int sortOrder, Instant uploadedAt) {
        this.id = id;
        this.listing = listing;
        this.s3Key = s3Key;
        this.sortOrder = sortOrder;
        this.uploadedAt = uploadedAt;
    }

    public UUID getId() { return id; }
    public ListingJpaEntity getListing() { return listing; }
    public String getS3Key() { return s3Key; }
    public int getSortOrder() { return sortOrder; }
    public Instant getUploadedAt() { return uploadedAt; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
