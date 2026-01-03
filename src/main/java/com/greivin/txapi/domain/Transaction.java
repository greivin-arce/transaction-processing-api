package com.greivin.txapi.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.greivin.txapi.domain.enums.TransactionType;

@Getter
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "amount_cents", nullable = false)
    private long amountCents;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "idempotency_key", length = 80)
    private String idempotencyKey;

    @Column(length = 200)
    private String description;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    protected Transaction() {
    }

    public Transaction(
            Account account,
            TransactionType type,
            long amountCents,
            String idempotencyKey,
            String description) {
        this.account = account;
        this.type = type;
        this.amountCents = amountCents;
        this.idempotencyKey = idempotencyKey;
        this.description = description;
    }
}