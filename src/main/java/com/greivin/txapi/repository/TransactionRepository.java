package com.greivin.txapi.repository;

import com.greivin.txapi.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @EntityGraph(attributePaths = "account")
    Optional<Transaction> findByAccount_IdAndIdempotencyKey(UUID accountId, String idempotencyKey);

    @EntityGraph(attributePaths = "account")
    Page<Transaction> findByAccount_ExternalId(String externalId, Pageable pageable);
}