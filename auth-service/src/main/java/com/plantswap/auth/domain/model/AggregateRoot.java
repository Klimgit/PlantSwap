package com.plantswap.auth.domain.model;

import com.plantswap.auth.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Базовый класс для всех корней агрегатов.
 * Накапливает доменные события, которые публикуются application-сервисом
 * после успешного сохранения в репозитории — доменный слой остаётся
 * независимым от инфраструктурных деталей.
 */
public abstract class AggregateRoot {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = Collections.unmodifiableList(new ArrayList<>(domainEvents));
        domainEvents.clear();
        return events;
    }
}
