package com.shop.shared.domain;

public final class IllegalStateTransitionException extends DomainException {
    public IllegalStateTransitionException(String currentState, String requestedState) {
        super("Cannot transition from " + currentState + " to " + requestedState);
    }
}