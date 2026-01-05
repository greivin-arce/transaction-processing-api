package com.greivin.txapi.repository;

import com.greivin.txapi.domain.Transaction;
import com.greivin.txapi.dto.BalanceHistoryItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @EntityGraph(attributePaths = "account")
    Optional<Transaction> findByAccount_IdAndIdempotencyKey(UUID accountId, String idempotencyKey);

    @EntityGraph(attributePaths = "account")
    Page<Transaction> findByAccount_ExternalId(String externalId, Pageable pageable);

    @Query("""
                select new com.greivin.txapi.dto.BalanceHistoryItem(
                    t.id,
                    t.type,
                    t.amountCents,
                    t.balanceAfterCents,
                    t.createdAt
                )
                from Transaction t
                where t.account.externalId = :externalId
            """)
    Page<BalanceHistoryItem> balanceHistoryByAccountExternalId(String externalId, Pageable pageable);
}