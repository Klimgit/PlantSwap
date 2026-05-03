package com.plantswap.chat.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface SpringDataMessageRepository extends JpaRepository<MessageJpaEntity, UUID> {

    List<MessageJpaEntity> findByConversationIdOrderBySentAtDesc(
            UUID conversationId, Pageable pageable);

    long countByConversationId(UUID conversationId);
}
