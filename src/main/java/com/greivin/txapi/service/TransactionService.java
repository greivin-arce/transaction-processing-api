package com.greivin.txapi.service;

import com.greivin.txapi.domain.Account;
import com.greivin.txapi.domain.Transaction;
import com.greivin.txapi.domain.enums.TransactionType;
import com.greivin.txapi.dto.TransactionRequest;
import com.greivin.txapi.dto.TransactionResponse;
import com.greivin.txapi.repository.AccountRepository;
import com.greivin.txapi.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository,
            TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse deposit(String externalId, TransactionRequest req) {

        Account account = accountRepository.findByExternalId(externalId)
                .orElseThrow(() -> new AccountNotFoundException(externalId));

        String key = normalizeKey(req.idempotencyKey());
        if (key != null) {
            var existing = transactionRepository.findByAccount_IdAndIdempotencyKey(account.getId(), key);
            if (existing.isPresent()) {
                return TransactionResponse.from(existing.get(), account);
            }
        }

        Transaction tx = new Transaction(
                account,
                TransactionType.DEPOSIT,
                req.amountCents(),
                key,
                req.description());

        account.setBalanceCents(account.getBalanceCents() + req.amountCents());

        transactionRepository.save(tx);
        accountRepository.save(account);

        return TransactionResponse.from(tx, account);
    }

    private String normalizeKey(String key) {
        if (key == null)
            return null;
        String trimmed = key.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @Transactional
    public TransactionResponse withdraw(String externalId, TransactionRequest req) {

        Account account = accountRepository.findByExternalId(externalId)
                .orElseThrow(() -> new AccountNotFoundException(externalId));

        String key = normalizeKey(req.idempotencyKey());
        if (key != null) {
            var existing = transactionRepository.findByAccount_IdAndIdempotencyKey(account.getId(), key);
            if (existing.isPresent()) {
                return TransactionResponse.from(existing.get(), account);
            }
        }

        if (account.getBalanceCents() < req.amountCents()) {
            throw new IllegalArgumentException("Insufficient funds");
            // mejor luego: InsufficientFundsException
        }

        Transaction tx = new Transaction(
                account,
                TransactionType.WITHDRAW,
                req.amountCents(),
                key,
                req.description());

        account.setBalanceCents(account.getBalanceCents() - req.amountCents());

        transactionRepository.save(tx);
        accountRepository.save(account);

        return TransactionResponse.from(tx, account);
    }
}