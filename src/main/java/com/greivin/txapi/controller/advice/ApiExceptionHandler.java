package com.greivin.txapi.controller.advice;

import com.greivin.txapi.dto.ApiError;
import com.greivin.txapi.exception.AccountNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.greivin.txapi.exception.InsufficientFundsException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiError> handleAccountNotFound(AccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("ACCOUNT_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
        String msg = (ex.getMessage() == null || ex.getMessage().isBlank())
                ? "Invalid request"
                : ex.getMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of("BAD_REQUEST", msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {

        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .distinct()
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation error");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of("VALIDATION_ERROR", msg));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiError> handleInsufficientFunds(InsufficientFundsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("INSUFFICIENT_FUNDS", ex.getMessage()));
    }

    @ExceptionHandler({ org.springframework.orm.ObjectOptimisticLockingFailureException.class,
            jakarta.persistence.OptimisticLockException.class })
    public ResponseEntity<ApiError> handleOptimisticLock(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("CONCURRENT_UPDATE", "Account was updated concurrently. Retry the request."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of("BAD_REQUEST", "Malformed JSON request body"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of("INTERNAL_ERROR", "Unexpected error occurred"));
    }
}