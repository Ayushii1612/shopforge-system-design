package com.shop.shared.domain;

import java.util.Objects;
import java.util.UUID;

public record EventId(UUID value) implements Identifier {
    public EventId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static EventId random() {
        return new EventId(UUID.randomUUID());
    }
}