package com.greivin.txapi.dto;

import java.util.UUID;

public record AccountResponse(
        UUID id,
        String externalId,
        String ownerName,
        String currency,
        long balanceCents,
        String status
) {}