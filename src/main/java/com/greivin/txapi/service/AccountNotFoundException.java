package com.greivin.txapi.service;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String externalId) {
        super("Account not found with externalId: " + externalId);
    }
}