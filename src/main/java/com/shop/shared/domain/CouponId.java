package com.shop.shared.domain;

import java.util.Objects;
import java.util.UUID;

public record CouponId(UUID value) implements Identifier {
    public CouponId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static CouponId random() {
        return new CouponId(UUID.randomUUID());
    }
}