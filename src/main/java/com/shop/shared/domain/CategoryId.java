package com.shop.shared.domain;

import java.util.Objects;
import java.util.UUID;

public record CategoryId(UUID value) implements Identifier {
    public CategoryId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static CategoryId random() {
        return new CategoryId(UUID.randomUUID());
    }
}