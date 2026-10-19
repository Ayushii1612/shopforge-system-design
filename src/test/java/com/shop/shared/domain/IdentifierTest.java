package com.shop.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class IdentifierTest {

    @Test
    void generatesDistinctTypedIdentifiers() {
        UserId firstUserId = UserId.random();
        UserId secondUserId = UserId.random();
        OrderId orderId = OrderId.random();

        assertThat(firstUserId).isNotEqualTo(secondUserId);
        assertThat(firstUserId).isNotEqualTo(orderId);
        assertThat(firstUserId.value()).isInstanceOf(UUID.class);
    }

    @Test
    void rejectsNullIdentifierValues() {
        assertThatThrownBy(() -> new UserId(null))
                .isInstanceOf(NullPointerException.class);
    }
}