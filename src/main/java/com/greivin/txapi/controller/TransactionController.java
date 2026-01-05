package com.greivin.txapi.controller;

import com.greivin.txapi.dto.BalanceHistoryItem;
import com.greivin.txapi.dto.TransactionRequest;
import com.greivin.txapi.dto.TransactionResponse;
import com.greivin.txapi.service.TransactionService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts/{externalId}")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse deposit(
            @PathVariable String externalId,
            @Valid @RequestBody TransactionRequest req) {
        return service.deposit(externalId, req);
    }

    @PostMapping("/withdraw")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse withdraw(
            @PathVariable String externalId,
            @Valid @RequestBody TransactionRequest req) {
        return service.withdraw(externalId, req);
    }

    @GetMapping("/transactions")
    public Page<TransactionResponse> list(
            @PathVariable String externalId,
            @org.springframework.data.web.PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable) {
        return service.listByExternalId(externalId, pageable);
    }

    @GetMapping("/balance-history")
    public Page<BalanceHistoryItem> balanceHistory(
            @PathVariable String externalId,
            Pageable pageable) {
        return service.balanceHistory(externalId, pageable);
    }

}