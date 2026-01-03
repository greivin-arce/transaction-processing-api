package com.greivin.txapi.repository;

import com.greivin.txapi.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByAccount_IdAndIdempotencyKey(UUID accountId, String idempotencyKey);
}