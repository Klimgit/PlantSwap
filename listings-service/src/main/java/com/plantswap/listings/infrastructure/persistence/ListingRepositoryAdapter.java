package com.plantswap.listings.infrastructure.persistence;

import com.plantswap.listings.domain.model.*;
import com.plantswap.listings.domain.repository.ListingFilter;
import com.plantswap.listings.domain.repository.ListingRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Адаптер репозитория объявлений.
 */
@Repository
public class ListingRepositoryAdapter implements ListingRepository {

    private final SpringDataListingRepository jpa;

    public ListingRepositoryAdapter(SpringDataListingRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Listing listing) {
        ListingJpaEntity entity = toEntity(listing);
        List<PhotoJpaEntity> photos = listing.photos().stream()
                .map(p -> new PhotoJpaEntity(p.id().value(), entity,
                        p.s3Key(), p.sortOrder(), Instant.now()))
                .toList();
        entity.setPhotos(photos);
        jpa.save(entity);
    }

    @Override
    public Optional<Listing> findById(ListingId id) {
        return jpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Listing> findAll(ListingFilter filter, int page, int size) {
        return jpa.findAll(toSpec(filter), PageRequest.of(page, size))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public long countAll(ListingFilter filter) {
        return jpa.count(toSpec(filter));
    }

    @Override
    public List<Listing> findByOwnerId(OwnerId ownerId, int page, int size) {
        ListingFilter filter = new ListingFilter(null, null, null, null, null, null);
        Specification<ListingJpaEntity> spec = toSpec(filter)
                .and((root, q, cb) -> cb.equal(root.get("ownerId"), ownerId.value()));
        return jpa.findAll(spec, PageRequest.of(page, size))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void delete(ListingId id) {
        jpa.deleteById(id.value());
    }

    // ── Specification для динамических фильтров ────────────────────────────

    private Specification<ListingJpaEntity> toSpec(ListingFilter f) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (f.status() != null)
                predicates.add(cb.equal(root.get("status"), f.status().name()));
            if (f.type() != null)
                predicates.add(cb.equal(root.get("type"), f.type().name()));
            if (f.city() != null && !f.city().isBlank())
                predicates.add(cb.like(cb.lower(root.get("city")),
                        "%" + f.city().toLowerCase() + "%"));
            if (f.searchQuery() != null && !f.searchQuery().isBlank()) {
                String q = "%" + f.searchQuery().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), q),
                        cb.like(cb.lower(root.get("description")), q)
                ));
            }
            if (f.priceMin() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("priceAmount"), f.priceMin()));
            if (f.priceMax() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("priceAmount"), f.priceMax()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ── Маппинг ────────────────────────────────────────────────────────────

    private ListingJpaEntity toEntity(Listing l) {
        return new ListingJpaEntity(
                l.id().value(), l.ownerId().value(), l.type().name(),
                l.title().value(), l.description().value(),
                l.price() != null ? l.price().amount() : null,
                l.price() != null ? l.price().currency() : null,
                l.city() != null ? l.city().value() : null,
                l.status().name(), l.createdAt(), l.updatedAt()
        );
    }

    private Listing toDomain(ListingJpaEntity e) {
        List<Photo> photos = e.getPhotos().stream()
                .map(p -> new Photo(PhotoId.of(p.getId()),
                        ListingId.of(e.getId()), p.getS3Key(), p.getSortOrder()))
                .toList();
        return Listing.reconstitute(
                ListingId.of(e.getId()), OwnerId.of(e.getOwnerId()),
                ListingType.valueOf(e.getType()),
                new Title(e.getTitle()),
                Description.ofNullable(e.getDescription()),
                e.getPriceAmount() != null ? new Price(e.getPriceAmount(), e.getPriceCurrency()) : null,
                City.ofNullable(e.getCity()),
                ListingStatus.valueOf(e.getStatus()),
                photos, e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
