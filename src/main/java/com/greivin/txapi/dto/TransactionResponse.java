package com.greivin.txapi.dto;

import com.greivin.txapi.domain.Transaction;

public record TransactionResponse(
        String id,
        String externalId,
        String type,
        long amountCents,
        long balanceCents,
        String idempotencyKey,
        String description) {
    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(
                tx.getId().toString(),
                tx.getAccount().getExternalId(),
                tx.getType().name(),
                tx.getAmountCents(),
                tx.getBalanceAfterCents(),
                tx.getIdempotencyKey(),
                tx.getDescription());
    }
}