package com.radix.loanpayment.common.exception;

/**
 * Thrown when a business rule is violated (e.g. overpayment on a loan).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
