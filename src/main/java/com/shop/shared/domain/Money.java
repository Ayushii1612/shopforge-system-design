package com.shop.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        int fractionDigits = currency.getDefaultFractionDigits();
        if (fractionDigits < 0) {
            throw new IllegalArgumentException("Currency must define fraction digits: " + currency);
        }
        amount = amount.setScale(fractionDigits, RoundingMode.HALF_EVEN);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(String amount, Currency currency) {
        return of(new BigDecimal(amount), currency);
    }

    public static Money zero(Currency currency) {
        return of(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return of(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return of(amount.subtract(other.amount), currency);
    }

    public Money multiply(BigDecimal multiplier) {
        Objects.requireNonNull(multiplier, "multiplier must not be null");
        return of(amount.multiply(multiplier), currency);
    }

    public Money negate() {
        return of(amount.negate(), currency);
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other must not be null");
        if (!currency.equals(other.currency)) {
            throw new CurrencyMismatchException(currency, other.currency);
        }
    }
}