package com.plantswap.chat.infrastructure.grpc;

import com.plantswap.chat.application.port.out.DealParticipantCheckPort;
import com.plantswap.deals.grpc.CheckParticipantRequest;
import com.plantswap.deals.grpc.CheckParticipantResponse;
import com.plantswap.deals.grpc.DealParticipantServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Адаптер gRPC-клиента для проверки участия пользователя в сделке.
 */
@Component
public class DealParticipantGrpcClient implements DealParticipantCheckPort {

    private static final Logger log = LoggerFactory.getLogger(DealParticipantGrpcClient.class);

    @GrpcClient("deals-service")
    private DealParticipantServiceGrpc.DealParticipantServiceBlockingStub stub;

    @Override
    public DealParticipants check(UUID dealId, UUID userId) {
        log.debug("gRPC CheckParticipant: dealId={}, userId={}", dealId, userId);

        CheckParticipantRequest request = CheckParticipantRequest.newBuilder()
                .setDealId(dealId.toString())
                .setUserId(userId.toString())
                .build();

        CheckParticipantResponse response = stub.checkParticipant(request);

        return new DealParticipants(
                response.getIsParticipant(),
                response.getOwnerId().isBlank() ? null : UUID.fromString(response.getOwnerId()),
                response.getRequesterId().isBlank() ? null : UUID.fromString(response.getRequesterId())
        );
    }
}
