package com.shop.shared.domain;

import java.util.Objects;
import java.util.UUID;

public record OrderId(UUID value) implements Identifier {
    public OrderId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static OrderId random() {
        return new OrderId(UUID.randomUUID());
    }
}