package com.greivin.txapi.dto;

import com.greivin.txapi.domain.Account;
import com.greivin.txapi.domain.Transaction;

public record TransactionResponse(
        String id,
        String externalId,
        String type,
        long amountCents,
        long balanceCents,
        String idempotencyKey,
        String description
) {
    public static TransactionResponse from(Transaction tx, Account account) {
        return new TransactionResponse(
                tx.getId().toString(),
                account.getExternalId(),
                tx.getType().name(),
                tx.getAmountCents(),
                account.getBalanceCents(),
                tx.getIdempotencyKey(),
                tx.getDescription()
        );
    }
}