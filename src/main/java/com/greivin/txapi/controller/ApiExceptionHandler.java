package com.greivin.txapi.controller;

import com.greivin.txapi.service.AccountNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAccountNotFound(AccountNotFoundException ex) {
        return Map.of(
                "code", "ACCOUNT_NOT_FOUND",
                "message", ex.getMessage()
        );
    }
}