package com.shop.shared.domain;

import java.util.Objects;
import java.util.UUID;

public record CartId(UUID value) implements Identifier {
    public CartId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static CartId random() {
        return new CartId(UUID.randomUUID());
    }
}