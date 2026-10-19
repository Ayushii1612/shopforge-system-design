package com.shop.shared.domain;

import java.util.Objects;
import java.util.UUID;

public record ReservationId(UUID value) implements Identifier {
    public ReservationId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static ReservationId random() {
        return new ReservationId(UUID.randomUUID());
    }
}