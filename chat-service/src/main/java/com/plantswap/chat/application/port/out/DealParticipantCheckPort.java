package com.plantswap.chat.application.port.out;

import java.util.UUID;

/**
 * Порт проверки участия пользователя в сделке через gRPC → deals-service.
 */
public interface DealParticipantCheckPort {
    DealParticipants check(UUID dealId, UUID userId);

    record DealParticipants(boolean isParticipant, UUID ownerId, UUID requesterId) {}
}
