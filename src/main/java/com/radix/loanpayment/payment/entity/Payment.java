package com.radix.loanpayment.payment.entity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * JPA entity representing a Payment made towards a {@code Loan}.
 *
 * <p>Each payment record captures the amount paid and the associated loan ID.
 * The actual balance reduction is applied to the Loan entity by the
 * {@code PaymentService}.
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "loan_id", nullable = false)
    private Long loanId;

    @Column(name = "payment_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal paymentAmount;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    protected Payment() {
        // Required by JPA
    }

    /**
     * Creates a new payment record.
     *
     * @param loanId        the ID of the loan this payment is applied to
     * @param paymentAmount the amount being paid
     */
    public Payment(Long loanId, BigDecimal paymentAmount) {
        this.loanId = loanId;
        this.paymentAmount = paymentAmount;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public Long getPaymentId() { return paymentId; }

    public Long getLoanId() { return loanId; }

    public BigDecimal getPaymentAmount() { return paymentAmount; }
}
