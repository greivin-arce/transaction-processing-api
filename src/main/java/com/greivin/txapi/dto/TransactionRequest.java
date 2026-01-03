package com.greivin.txapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TransactionRequest(

        @NotNull
        @Min(1)
        Long amountCents,

        String description,

        String idempotencyKey
) {}