package com.shop.shared.domain;

import java.util.Currency;

public final class CurrencyMismatchException extends DomainException {
    public CurrencyMismatchException(Currency expected, Currency actual) {
        super("Currency mismatch: expected " + expected + " but got " + actual);
    }
}