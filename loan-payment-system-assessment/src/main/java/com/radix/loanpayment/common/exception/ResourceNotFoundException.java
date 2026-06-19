package com.radix.loanpayment.common.exception;

/**
 * Thrown when a requested resource (e.g. a Loan) cannot be found in the system.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
