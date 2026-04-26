package com.plantswap.listings.application.port.out;

import com.plantswap.listings.domain.event.DomainEvent;

public interface EventPublisherPort {
    void publish(DomainEvent event);
}
