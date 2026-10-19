package com.shop.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class MoneyTest {

    private static final Currency USD = Currency.getInstance("USD");

    @Test
    void addsAndSubtractsValuesInTheSameCurrency() {
        Money subtotal = Money.of("10.25", USD);
        Money fee = Money.of("1.50", USD);

        assertThat(subtotal.add(fee)).isEqualTo(Money.of("11.75", USD));
        assertThat(subtotal.subtract(fee)).isEqualTo(Money.of("8.75", USD));
    }

    @Test
    void roundsToCurrencyScaleUsingHalfEven() {
        assertThat(Money.of("1.005", USD)).isEqualTo(Money.of("1.00", USD));
        assertThat(Money.of("1.015", USD)).isEqualTo(Money.of("1.02", USD));
        assertThat(Money.of("1.00", USD).multiply(new BigDecimal("1.005")))
                .isEqualTo(Money.of("1.00", USD));
    }

    @Test
    void usesCurrencySpecificScale() {
        Currency jpy = Currency.getInstance("JPY");

        assertThat(Money.of("12.6", jpy)).isEqualTo(Money.of("13", jpy));
    }

    @Test
    void rejectsArithmeticAcrossCurrencies() {
        Money usd = Money.of("10.00", USD);
        Money eur = Money.of("10.00", Currency.getInstance("EUR"));

        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(CurrencyMismatchException.class);
    }
}