package com.shop.shared.domain;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) implements Identifier {
    public UserId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static UserId random() {
        return new UserId(UUID.randomUUID());
    }
}