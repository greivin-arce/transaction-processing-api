package com.greivin.txapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TransactionRequest(

                @NotNull @Min(1) Long amountCents,
                @Size(max = 200) String description,
                @Size(max = 80) String idempotencyKey) {
}