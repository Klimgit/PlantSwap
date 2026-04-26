package com.plantswap.deals.application.port.out;

import com.plantswap.deals.domain.event.DomainEvent;

public interface EventPublisherPort {
    void publish(DomainEvent event);
}
