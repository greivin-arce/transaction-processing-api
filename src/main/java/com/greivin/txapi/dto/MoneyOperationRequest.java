package com.greivin.txapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record MoneyOperationRequest(
        @NotBlank String externalId,
        @Min(1) long amountCents,
        String idempotencyKey,
        String description
) {}