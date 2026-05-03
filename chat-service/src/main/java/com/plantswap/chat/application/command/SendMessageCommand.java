package com.plantswap.chat.application.command;

import java.util.UUID;

public record SendMessageCommand(
        UUID dealId,
        UUID senderId,
        String content
) {}
