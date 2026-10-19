package com.shop.shared.domain;

import java.util.Objects;
import java.util.UUID;

public record ShipmentId(UUID value) implements Identifier {
    public ShipmentId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static ShipmentId random() {
        return new ShipmentId(UUID.randomUUID());
    }
}