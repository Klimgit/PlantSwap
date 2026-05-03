package com.plantswap.chat.application.port.in;

import com.plantswap.chat.application.command.SendMessageCommand;
import com.plantswap.chat.application.result.MessageDto;

public interface SendMessageUseCase {
    MessageDto send(SendMessageCommand command);
}
