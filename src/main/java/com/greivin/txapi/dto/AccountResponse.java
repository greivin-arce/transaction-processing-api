package com.greivin.txapi.dto;

import com.greivin.txapi.domain.Account;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class AccountResponse {

    private UUID id;
    private String externalId;
    private String ownerName;
    private String currency;
    private long balanceCents;
    private String status;
    private OffsetDateTime createdAt;

    public static AccountResponse from(Account account) {
        AccountResponse res = new AccountResponse();
        res.id = account.getId();
        res.externalId = account.getExternalId();
        res.ownerName = account.getOwnerName();
        res.currency = account.getCurrency();
        res.balanceCents = account.getBalanceCents();
        res.status = account.getStatus();
        res.createdAt = account.getCreatedAt();
        return res;
    }
}