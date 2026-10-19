package com.shop.shared.domain;

import java.util.Objects;
import java.util.UUID;

public record WarehouseId(UUID value) implements Identifier {
    public WarehouseId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static WarehouseId random() {
        return new WarehouseId(UUID.randomUUID());
    }
}