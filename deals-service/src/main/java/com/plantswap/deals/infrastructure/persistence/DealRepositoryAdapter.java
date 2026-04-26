package com.plantswap.deals.infrastructure.persistence;

import com.plantswap.deals.domain.model.*;
import com.plantswap.deals.domain.repository.DealRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DealRepositoryAdapter implements DealRepository {

    private static final List<String> ACTIVE_STATUSES = List.of(
            DealStatus.PENDING.name(), DealStatus.ACCEPTED.name());

    private final SpringDataDealRepository jpa;

    public DealRepositoryAdapter(SpringDataDealRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Deal deal) {
        jpa.save(toEntity(deal));
    }

    @Override
    public Optional<Deal> findById(DealId id) {
        return jpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Deal> findByUserId(UUID userId, int page, int size) {
        return jpa.findByUserId(userId, PageRequest.of(page, size))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public long countByUserId(UUID userId) {
        return jpa.countByUserId(userId);
    }

    @Override
    public List<Deal> findActiveByListingIdAndRequesterId(ListingId listingId,
                                                           RequesterId requesterId) {
        return jpa.findActiveByListingIdAndRequesterId(
                        listingId.value(), requesterId.value(), ACTIVE_STATUSES)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void delete(DealId id) {
        jpa.deleteById(id.value());
    }

    // ── Маппинг ────────────────────────────────────────────────────────────

    private DealJpaEntity toEntity(Deal d) {
        return new DealJpaEntity(
                d.id().value(), d.listingId().value(),
                d.ownerId().value(), d.requesterId().value(),
                d.status().name(), d.note().value(),
                d.createdAt(), d.updatedAt()
        );
    }

    private Deal toDomain(DealJpaEntity e) {
        return Deal.reconstitute(
                DealId.of(e.getId()),
                ListingId.of(e.getListingId()),
                OwnerId.of(e.getOwnerId()),
                RequesterId.of(e.getRequesterId()),
                DealNote.ofNullable(e.getNote()),
                DealStatus.valueOf(e.getStatus()),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
