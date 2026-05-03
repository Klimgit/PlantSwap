package com.plantswap.chat.application.port.in;

import com.plantswap.chat.application.result.MessageDto;
import com.plantswap.chat.application.result.PageDto;

import java.util.UUID;

public interface GetHistoryUseCase {
    PageDto<MessageDto> getHistory(UUID dealId, UUID requesterId, int page, int size);
}
