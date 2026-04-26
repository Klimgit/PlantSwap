package com.plantswap.deals.api.rest;

import com.plantswap.deals.api.rest.dto.*;
import com.plantswap.deals.application.command.CreateDealCommand;
import com.plantswap.deals.application.command.DealActionCommand;
import com.plantswap.deals.application.port.in.*;
import com.plantswap.deals.application.result.DealDto;
import com.plantswap.deals.application.result.PageDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/deals")
public class DealController {

    private final CreateDealUseCase createDeal;
    private final AcceptDealUseCase acceptDeal;
    private final RejectDealUseCase rejectDeal;
    private final CompleteDealUseCase completeDeal;
    private final CancelDealUseCase cancelDeal;
    private final GetDealUseCase getDeal;
    private final GetUserDealsUseCase getUserDeals;

    public DealController(CreateDealUseCase createDeal,
                           AcceptDealUseCase acceptDeal,
                           RejectDealUseCase rejectDeal,
                           CompleteDealUseCase completeDeal,
                           CancelDealUseCase cancelDeal,
                           GetDealUseCase getDeal,
                           GetUserDealsUseCase getUserDeals) {
        this.createDeal = createDeal;
        this.acceptDeal = acceptDeal;
        this.rejectDeal = rejectDeal;
        this.completeDeal = completeDeal;
        this.cancelDeal = cancelDeal;
        this.getDeal = getDeal;
        this.getUserDeals = getUserDeals;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DealResponse create(@Valid @RequestBody CreateDealRequest request,
                                @RequestHeader("X-User-Id") UUID currentUserId) {
        DealDto dto = createDeal.create(new CreateDealCommand(
                request.listingId(), request.ownerId(), currentUserId, request.note()));
        return toResponse(dto);
    }

    @GetMapping("/{id}")
    public DealResponse get(@PathVariable UUID id,
                             @RequestHeader("X-User-Id") UUID currentUserId) {
        return toResponse(getDeal.getDeal(id, currentUserId));
    }

    @GetMapping("/my")
    public PageResponse<DealResponse> my(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("X-User-Id") UUID currentUserId) {
        PageDto<DealDto> result = getUserDeals.getUserDeals(currentUserId, page, size);
        return new PageResponse<>(
                result.content().stream().map(this::toResponse).toList(),
                result.page(), result.size(), result.totalElements(), result.totalPages());
    }

    @PostMapping("/{id}/accept")
    public DealResponse accept(@PathVariable UUID id,
                                @RequestHeader("X-User-Id") UUID currentUserId) {
        return toResponse(acceptDeal.accept(new DealActionCommand(id, currentUserId)));
    }

    @PostMapping("/{id}/reject")
    public DealResponse reject(@PathVariable UUID id,
                                @RequestHeader("X-User-Id") UUID currentUserId) {
        return toResponse(rejectDeal.reject(new DealActionCommand(id, currentUserId)));
    }

    @PostMapping("/{id}/complete")
    public DealResponse complete(@PathVariable UUID id,
                                  @RequestHeader("X-User-Id") UUID currentUserId) {
        return toResponse(completeDeal.complete(new DealActionCommand(id, currentUserId)));
    }

    @PostMapping("/{id}/cancel")
    public DealResponse cancel(@PathVariable UUID id,
                                @RequestHeader("X-User-Id") UUID currentUserId) {
        return toResponse(cancelDeal.cancel(new DealActionCommand(id, currentUserId)));
    }

    private DealResponse toResponse(DealDto dto) {
        return new DealResponse(dto.id(), dto.listingId(), dto.ownerId(),
                dto.requesterId(), dto.status(), dto.note(),
                dto.createdAt(), dto.updatedAt());
    }
}
