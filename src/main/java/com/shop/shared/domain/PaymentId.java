package com.shop.shared.domain;

import java.util.Objects;
import java.util.UUID;

public record PaymentId(UUID value) implements Identifier {
    public PaymentId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static PaymentId random() {
        return new PaymentId(UUID.randomUUID());
    }
}