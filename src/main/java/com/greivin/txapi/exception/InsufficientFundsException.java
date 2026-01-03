package com.greivin.txapi.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(long balanceCents, long requestedCents) {
        super("Insufficient funds: balance=" + balanceCents + ", requested=" + requestedCents);
    }
}