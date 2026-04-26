package com.plantswap.deals.application.service;

import com.plantswap.deals.application.command.CreateDealCommand;
import com.plantswap.deals.application.command.DealActionCommand;
import com.plantswap.deals.application.port.in.*;
import com.plantswap.deals.application.port.out.EventPublisherPort;
import com.plantswap.deals.application.result.DealDto;
import com.plantswap.deals.application.result.PageDto;
import com.plantswap.deals.domain.model.*;
import com.plantswap.deals.domain.repository.DealRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application-сервис контекста Deals.
 * Оркеструет агрегат Deal и порты.
 */
@Service
@Transactional
public class DealService implements
        CreateDealUseCase,
        AcceptDealUseCase,
        RejectDealUseCase,
        CompleteDealUseCase,
        CancelDealUseCase,
        GetDealUseCase,
        GetUserDealsUseCase {

    private static final Logger log = LoggerFactory.getLogger(DealService.class);

    private final DealRepository dealRepository;
    private final EventPublisherPort eventPublisher;

    public DealService(DealRepository dealRepository, EventPublisherPort eventPublisher) {
        this.dealRepository = dealRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public DealDto create(CreateDealCommand cmd) {
        ListingId listingId = ListingId.of(cmd.listingId());
        RequesterId requesterId = RequesterId.of(cmd.requesterId());

        List<Deal> existing = dealRepository.findActiveByListingIdAndRequesterId(
                listingId, requesterId);
        if (!existing.isEmpty())
            throw new DealAlreadyExistsException(listingId, requesterId);

        Deal deal = Deal.create(
                listingId,
                OwnerId.of(cmd.ownerId()),
                requesterId,
                DealNote.ofNullable(cmd.note())
        );
        dealRepository.save(deal);
        publishAndClear(deal);

        log.info("Сделка создана: id={}, listingId={}, requesterId={}",
                deal.id(), cmd.listingId(), cmd.requesterId());
        return toDto(deal);
    }

    @Override
    public DealDto accept(DealActionCommand cmd) {
        Deal deal = getOrThrow(DealId.of(cmd.dealId()));
        deal.accept(cmd.actorId());
        dealRepository.save(deal);
        publishAndClear(deal);
        log.info("Сделка принята: id={}", cmd.dealId());
        return toDto(deal);
    }

    @Override
    public DealDto reject(DealActionCommand cmd) {
        Deal deal = getOrThrow(DealId.of(cmd.dealId()));
        deal.reject(cmd.actorId());
        dealRepository.save(deal);
        publishAndClear(deal);
        log.info("Сделка отклонена: id={}", cmd.dealId());
        return toDto(deal);
    }

    @Override
    public DealDto complete(DealActionCommand cmd) {
        Deal deal = getOrThrow(DealId.of(cmd.dealId()));
        deal.complete(cmd.actorId());
        dealRepository.save(deal);
        publishAndClear(deal);
        log.info("Сделка завершена: id={}", cmd.dealId());
        return toDto(deal);
    }

    @Override
    public DealDto cancel(DealActionCommand cmd) {
        Deal deal = getOrThrow(DealId.of(cmd.dealId()));
        deal.cancel(cmd.actorId());
        dealRepository.save(deal);
        publishAndClear(deal);
        log.info("Сделка отменена: id={}, actor={}", cmd.dealId(), cmd.actorId());
        return toDto(deal);
    }

    @Override
    @Transactional(readOnly = true)
    public DealDto getDeal(UUID dealId, UUID actorId) {
        Deal deal = getOrThrow(DealId.of(dealId));
        if (!deal.isParticipant(actorId))
            throw new DealAccessDeniedException(deal.id(), actorId);
        return toDto(deal);
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<DealDto> getUserDeals(UUID userId, int page, int size) {
        List<DealDto> dtos = dealRepository.findByUserId(userId, page, size)
                .stream().map(this::toDto).toList();
        long total = dealRepository.countByUserId(userId);
        return PageDto.of(dtos, page, size, total);
    }

    // ── Вспомогательные методы ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public boolean isParticipant(UUID dealId, UUID userId) {
        return dealRepository.findById(DealId.of(dealId))
                .map(d -> d.isParticipant(userId))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Deal findByIdForGrpc(UUID dealId) {
        return dealRepository.findById(DealId.of(dealId))
                .orElseThrow(() -> DealNotFoundException.byId(DealId.of(dealId)));
    }

    private Deal getOrThrow(DealId id) {
        return dealRepository.findById(id)
                .orElseThrow(() -> DealNotFoundException.byId(id));
    }

    private void publishAndClear(Deal deal) {
        deal.pullDomainEvents().forEach(eventPublisher::publish);
    }

    DealDto toDto(Deal d) {
        return new DealDto(
                d.id().value(), d.listingId().value(),
                d.ownerId().value(), d.requesterId().value(),
                d.status().name(),
                d.note().value(),
                d.createdAt(), d.updatedAt()
        );
    }
}
