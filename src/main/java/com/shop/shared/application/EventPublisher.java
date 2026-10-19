package com.shop.shared.application;

import com.shop.shared.domain.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}