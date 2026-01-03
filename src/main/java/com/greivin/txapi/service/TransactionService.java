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

        account.setBalanceCents(account.getBalanceCents() + req.amountCents());

        Transaction tx = new Transaction(
                account,
                TransactionType.DEPOSIT,
                req.amountCents(),
                req.idempotencyKey(),
                req.description());

        transactionRepository.save(tx);
        accountRepository.save(account);

        return TransactionResponse.from(tx);
    }

    @Transactional
    public TransactionResponse withdraw(String externalId, TransactionRequest req) {

        Account account = accountRepository.findByExternalId(externalId)
                .orElseThrow(() -> new AccountNotFoundException(externalId));

        if (account.getBalanceCents() < req.amountCents()) {
            throw new IllegalStateException("Insufficient balance");
        }

        account.setBalanceCents(account.getBalanceCents() - req.amountCents());

        Transaction tx = new Transaction(
                account,
                TransactionType.WITHDRAW,
                req.amountCents(),
                req.idempotencyKey(),
                req.description());

        transactionRepository.save(tx);
        accountRepository.save(account);

        return TransactionResponse.from(tx);
    }
}