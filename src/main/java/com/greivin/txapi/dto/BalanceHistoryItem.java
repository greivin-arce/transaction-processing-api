package com.greivin.txapi.dto;

import com.greivin.txapi.domain.enums.TransactionType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BalanceHistoryItem(
        UUID id,
        TransactionType type,
        long amountCents,
        Long balanceAfterCents,
        OffsetDateTime createdAt) {
}