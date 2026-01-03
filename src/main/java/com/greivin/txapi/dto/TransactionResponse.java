package com.greivin.txapi.dto;

import com.greivin.txapi.domain.Transaction;
import com.greivin.txapi.domain.enums.TransactionType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        TransactionType type,
        long amountCents,
        String currency,
        String description,
        OffsetDateTime createdAt
) {
    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getType(),
                tx.getAmountCents(),
                tx.getCurrency(),
                tx.getDescription(),
                tx.getCreatedAt()
        );
    }
}