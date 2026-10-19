package com.shop.shared.domain;

import java.time.Instant;

public interface DomainEvent {
    EventId eventId();

    Instant occurredAt();
}