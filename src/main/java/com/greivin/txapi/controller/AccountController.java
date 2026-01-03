package com.greivin.txapi.controller;

import com.greivin.txapi.dto.AccountResponse;
import com.greivin.txapi.dto.CreateAccountRequest;
import com.greivin.txapi.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest req) {
        return service.create(req);
    }

    @GetMapping("/{externalId}")
    public AccountResponse getByExternalId(@PathVariable String externalId) {
        return service.getByExternalId(externalId);
    }
}