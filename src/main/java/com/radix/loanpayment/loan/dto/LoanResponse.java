package com.radix.loanpayment.loan.dto;

import com.radix.loanpayment.loan.entity.Loan;
import com.radix.loanpayment.loan.entity.LoanStatus;

import java.math.BigDecimal;

/**
 * Response payload returned by the Loan API endpoints.
 */
public class LoanResponse {

    private Long loanId;
    private BigDecimal loanAmount;
    private Integer term;
    private BigDecimal remainingBalance;
    private LoanStatus status;

    // -------------------------------------------------------------------------
    // Factory method
    // -------------------------------------------------------------------------

    /**
     * Constructs a {@code LoanResponse} from a {@link Loan} entity.
     *
     * @param loan the loan entity to map
     * @return a populated response DTO
     */
    public static LoanResponse from(Loan loan) {
        LoanResponse response = new LoanResponse();
        response.loanId = loan.getLoanId();
        response.loanAmount = loan.getLoanAmount();
        response.term = loan.getTerm();
        response.remainingBalance = loan.getRemainingBalance();
        response.status = loan.getStatus();
        return response;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public Long getLoanId() { return loanId; }

    public BigDecimal getLoanAmount() { return loanAmount; }

    public Integer getTerm() { return term; }

    public BigDecimal getRemainingBalance() { return remainingBalance; }

    public LoanStatus getStatus() { return status; }
}
