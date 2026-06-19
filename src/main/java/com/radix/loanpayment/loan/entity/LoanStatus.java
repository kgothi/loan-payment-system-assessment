package com.radix.loanpayment.loan.entity;

/**
 * Represents the lifecycle state of a {@link Loan}.
 *
 * <ul>
 *   <li>{@code ACTIVE}  – the loan has an outstanding balance.</li>
 *   <li>{@code SETTLED} – the loan has been fully repaid.</li>
 * </ul>
 */
public enum LoanStatus {
    ACTIVE,
    SETTLED
}
