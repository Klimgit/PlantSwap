package com.plantswap.deals.infrastructure.grpc;

import com.plantswap.deals.application.service.DealService;
import com.plantswap.deals.domain.model.Deal;
import com.plantswap.deals.domain.model.DealNotFoundException;
import com.plantswap.deals.grpc.CheckParticipantRequest;
import com.plantswap.deals.grpc.CheckParticipantResponse;
import com.plantswap.deals.grpc.DealParticipantServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * gRPC-сервис проверки участия пользователя в сделке.
 *
 * Используется chat-service перед разрешением отправки сообщений:
 * только участники сделки (owner и requester) могут писать в её чат.
 */
@GrpcService
public class DealParticipantGrpcService
        extends DealParticipantServiceGrpc.DealParticipantServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(DealParticipantGrpcService.class);

    private final DealService dealService;

    public DealParticipantGrpcService(DealService dealService) {
        this.dealService = dealService;
    }

    @Override
    public void checkParticipant(CheckParticipantRequest request,
                                  StreamObserver<CheckParticipantResponse> responseObserver) {
        try {
            UUID dealId = UUID.fromString(request.getDealId());
            UUID userId = UUID.fromString(request.getUserId());

            Deal deal = dealService.findByIdForGrpc(dealId);
            boolean isParticipant = deal.isParticipant(userId);

            CheckParticipantResponse response = CheckParticipantResponse.newBuilder()
                    .setIsParticipant(isParticipant)
                    .setOwnerId(deal.ownerId().value().toString())
                    .setRequesterId(deal.requesterId().value().toString())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.debug("CheckParticipant: dealId={}, userId={}, isParticipant={}",
                    dealId, userId, isParticipant);

        } catch (DealNotFoundException e) {
            responseObserver.onNext(CheckParticipantResponse.newBuilder()
                    .setIsParticipant(false)
                    .build());
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            log.warn("CheckParticipant: некорректный UUID в запросе: {}", e.getMessage());
            responseObserver.onError(
                    io.grpc.Status.INVALID_ARGUMENT
                            .withDescription("Некорректный формат UUID")
                            .asRuntimeException());
        }
    }
}
