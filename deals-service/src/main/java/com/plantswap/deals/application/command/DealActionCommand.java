package com.plantswap.deals.application.command;

import java.util.UUID;

public record DealActionCommand(
        UUID dealId,
        UUID actorId
) {}
