package com.radix.loanpayment.loan.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Request payload for creating a new loan via {@code POST /loans}.
 */
public class CreateLoanRequest {

    @NotNull(message = "loanAmount is required")
    @DecimalMin(value = "0.01", message = "loanAmount must be greater than zero")
    private BigDecimal loanAmount;

    @NotNull(message = "term is required")
    @Min(value = 1, message = "term must be at least 1 month")
    private Integer term;

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public BigDecimal getLoanAmount() { return loanAmount; }

    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }

    public Integer getTerm() { return term; }

    public void setTerm(Integer term) { this.term = term; }
}
