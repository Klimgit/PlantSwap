package com.plantswap.chat.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataConversationRepository extends JpaRepository<ConversationJpaEntity, UUID> {}
