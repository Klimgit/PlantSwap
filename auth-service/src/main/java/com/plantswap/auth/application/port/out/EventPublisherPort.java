package com.plantswap.auth.application.port.out;

import com.plantswap.auth.domain.event.DomainEvent;

public interface EventPublisherPort {
    void publish(DomainEvent event);
}
