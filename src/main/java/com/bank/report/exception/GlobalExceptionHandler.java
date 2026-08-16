package com.bank.report.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.Map;

/**
 * Centralized exception translation.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles domain errors.
     *
     * @param ex       business exception
     * @param exchange current exchange
     * @return error body
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex, ServerWebExchange exchange) {
        log.warn("Business error: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", ex.getStatus().value(),
                "message", ex.getMessage(),
                "path", exchange.getRequest().getPath().value()
        ));
    }

    /**
     * Handles unexpected errors.
     *
     * @param ex       exception
     * @param exchange current exchange
     * @return error body
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, ServerWebExchange exchange) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 500,
                "message", "Unexpected server error",
                "path", exchange.getRequest().getPath().value()
        ));
    }
}
