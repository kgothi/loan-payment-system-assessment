package com.radix.loanpayment.payment.dto;

import com.radix.loanpayment.loan.dto.LoanResponse;
import com.radix.loanpayment.payment.entity.Payment;

import java.math.BigDecimal;

/**
 * Response payload returned after a payment is successfully recorded.
 *
 * <p>Includes the payment record details as well as the updated loan state,
 * so the caller can immediately see the new remaining balance and status.
 */
public class PaymentResponse {

    private Long paymentId;
    private Long loanId;
    private BigDecimal paymentAmount;
    private LoanResponse updatedLoan;

    // -------------------------------------------------------------------------
    // Factory method
    // -------------------------------------------------------------------------

    /**
     * Constructs a {@code PaymentResponse} from a saved payment and the updated loan.
     *
     * @param payment     the persisted payment entity
     * @param updatedLoan the loan response after the payment was applied
     * @return a populated response DTO
     */
    public static PaymentResponse from(Payment payment, LoanResponse updatedLoan) {
        PaymentResponse response = new PaymentResponse();
        response.paymentId = payment.getPaymentId();
        response.loanId = payment.getLoanId();
        response.paymentAmount = payment.getPaymentAmount();
        response.updatedLoan = updatedLoan;
        return response;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public Long getPaymentId() { return paymentId; }

    public Long getLoanId() { return loanId; }

    public BigDecimal getPaymentAmount() { return paymentAmount; }

    public LoanResponse getUpdatedLoan() { return updatedLoan; }
}
