package com.radix.loanpayment.payment.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Request payload for recording a payment via {@code POST /payments}.
 */
public class CreatePaymentRequest {

    @NotNull(message = "loanId is required")
    private Long loanId;

    @NotNull(message = "paymentAmount is required")
    @DecimalMin(value = "0.01", message = "paymentAmount must be greater than zero")
    private BigDecimal paymentAmount;

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Long getLoanId() { return loanId; }

    public void setLoanId(Long loanId) { this.loanId = loanId; }

    public BigDecimal getPaymentAmount() { return paymentAmount; }

    public void setPaymentAmount(BigDecimal paymentAmount) { this.paymentAmount = paymentAmount; }
}
