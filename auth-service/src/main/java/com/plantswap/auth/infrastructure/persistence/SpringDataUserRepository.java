package com.plantswap.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

/** Spring Data JPA интерфейс для таблицы users. */
interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);

    Optional<UserJpaEntity> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE UserJpaEntity u SET u.username = :username, u.city = :city, u.updatedAt = :updatedAt WHERE u.id = :id")
    void updateProfile(UUID id, String username, String city, java.time.Instant updatedAt);
}
