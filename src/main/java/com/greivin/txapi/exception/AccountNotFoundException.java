package com.greivin.txapi.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String externalId) {
        super("Account not found: " + externalId);
    }
}
