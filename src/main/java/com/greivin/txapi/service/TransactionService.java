package com.greivin.txapi.service;

import com.greivin.txapi.domain.Account;
import com.greivin.txapi.domain.Transaction;
import com.greivin.txapi.domain.enums.TransactionType;
import com.greivin.txapi.dto.TransactionRequest;
import com.greivin.txapi.dto.TransactionResponse;
import com.greivin.txapi.repository.AccountRepository;
import com.greivin.txapi.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.greivin.txapi.exception.AccountNotFoundException;
import com.greivin.txapi.exception.InsufficientFundsException;

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
                return TransactionResponse.from(existing.get());
            }
        }

        long newBalance = account.getBalanceCents() + req.amountCents();
        account.setBalanceCents(newBalance);

        Transaction tx = new Transaction(
                account,
                TransactionType.DEPOSIT,
                req.amountCents(),
                key,
                req.description());
        tx.setBalanceAfterCents(newBalance);

        transactionRepository.save(tx);
        accountRepository.save(account);

        return TransactionResponse.from(tx);
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
                return TransactionResponse.from(existing.get()); // <- no ocupás Account
            }
        }

        long balance = account.getBalanceCents();
        long amount = req.amountCents();

        if (balance < amount) {
            throw new InsufficientFundsException(balance, amount);
        }

        long newBalance = balance - amount;
        account.setBalanceCents(newBalance);

        Transaction tx = new Transaction(
                account,
                TransactionType.WITHDRAW,
                amount,
                key,
                req.description());

        tx.setBalanceAfterCents(newBalance);

        transactionRepository.save(tx);
        accountRepository.save(account);

        return TransactionResponse.from(tx);
    }

    public Page<TransactionResponse> listByExternalId(String externalId, Pageable pageable) {
        var account = accountRepository.findByExternalId(externalId)
                .orElseThrow(() -> new AccountNotFoundException(externalId));

        return transactionRepository.findByAccount_ExternalId(externalId, pageable)
                .map(tx -> TransactionResponse.from(tx));
    }
}