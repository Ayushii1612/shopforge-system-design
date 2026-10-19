package com.shop.shared.domain;

import java.util.Objects;
import java.util.UUID;

public record ProductId(UUID value) implements Identifier {
    public ProductId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static ProductId random() {
        return new ProductId(UUID.randomUUID());
    }
}