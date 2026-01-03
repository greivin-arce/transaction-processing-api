package com.greivin.txapi.controller;

import com.greivin.txapi.dto.TransactionRequest;
import com.greivin.txapi.dto.TransactionResponse;
import com.greivin.txapi.service.TransactionService;
import jakarta.validation.Valid;
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
            @Valid @RequestBody TransactionRequest req
    ) {
        return service.deposit(externalId, req);
    }

    @PostMapping("/withdraw")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse withdraw(
            @PathVariable String externalId,
            @Valid @RequestBody TransactionRequest req
    ) {
        return service.withdraw(externalId, req);
    }
    
}