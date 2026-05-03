package com.plantswap.chat.api.rest;

import com.plantswap.chat.api.rest.dto.MessageResponse;
import com.plantswap.chat.api.rest.dto.PageResponse;
import com.plantswap.chat.application.port.in.GetHistoryUseCase;
import com.plantswap.chat.application.result.MessageDto;
import com.plantswap.chat.application.result.PageDto;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST API чата.
 */
@RestController
@RequestMapping("/conversations")
public class ConversationController {

    private final GetHistoryUseCase getHistory;

    public ConversationController(GetHistoryUseCase getHistory) {
        this.getHistory = getHistory;
    }

    @GetMapping("/{dealId}/messages")
    public PageResponse<MessageResponse> getMessages(
            @PathVariable UUID dealId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestHeader("X-User-Id") UUID currentUserId) {

        PageDto<MessageDto> result = getHistory.getHistory(dealId, currentUserId, page, size);
        return new PageResponse<>(
                result.content().stream().map(this::toResponse).toList(),
                result.page(), result.size(), result.totalElements(), result.totalPages());
    }

    private MessageResponse toResponse(MessageDto dto) {
        return new MessageResponse(dto.id(), dto.conversationId(),
                dto.senderId(), dto.content(), dto.sentAt());
    }
}
