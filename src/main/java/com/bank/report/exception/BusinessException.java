package com.bank.report.exception;

import org.springframework.http.HttpStatus;

/**
 * Domain exception translated to an HTTP status.
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    /**
     * @param message error message
     * @param status  HTTP status
     */
    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
