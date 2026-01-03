package com.greivin.txapi.service;

import com.greivin.txapi.domain.Account;
import com.greivin.txapi.dto.AccountResponse;
import com.greivin.txapi.dto.CreateAccountRequest;
import com.greivin.txapi.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public AccountResponse create(CreateAccountRequest req) {
        Account acc = new Account();
        acc.setExternalId(UUID.randomUUID().toString());
        acc.setOwnerName(req.ownerName());

        Account saved = repository.save(acc);
        return AccountResponse.from(saved);
    }

    public AccountResponse getByExternalId(String externalId) {
        Account account = repository.findByExternalId(externalId)
                .orElseThrow(() -> new AccountNotFoundException(externalId));

        return AccountResponse.from(account);
    }
}