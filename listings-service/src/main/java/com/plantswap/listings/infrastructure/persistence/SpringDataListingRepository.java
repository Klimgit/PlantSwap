package com.plantswap.listings.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/** Spring Data JPA интерфейс для объявлений. */
interface SpringDataListingRepository
        extends JpaRepository<ListingJpaEntity, UUID>,
                JpaSpecificationExecutor<ListingJpaEntity> {
}
