package com.radix.loanpayment.loan.entity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * JPA entity representing a Loan in the system.
 *
 * <p>A loan has an initial {@code loanAmount}, a {@code term} (duration in months),
 * a {@code remainingBalance} that decreases as payments are applied, and a
 * {@code status} that transitions from {@code ACTIVE} to {@code SETTLED} once the
 * balance reaches zero.
 */
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "loan_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "term", nullable = false)
    private Integer term;

    @Column(name = "remaining_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal remainingBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private LoanStatus status;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    protected Loan() {
        // Required by JPA
    }

    /**
     * Creates a new active loan.
     *
     * @param loanAmount the total loan amount (must be positive)
     * @param term       the loan duration in months (must be positive)
     */
    public Loan(BigDecimal loanAmount, Integer term) {
        this.loanAmount = loanAmount;
        this.term = term;
        this.remainingBalance = loanAmount;
        this.status = LoanStatus.ACTIVE;
    }

    // -------------------------------------------------------------------------
    // Business methods
    // -------------------------------------------------------------------------

    /**
     * Applies a payment to this loan, reducing the remaining balance.
     * Transitions the loan to {@code SETTLED} when the balance reaches zero.
     *
     * @param paymentAmount the amount being paid (must not exceed remaining balance)
     */
    public void applyPayment(BigDecimal paymentAmount) {
        this.remainingBalance = this.remainingBalance.subtract(paymentAmount);
        if (this.remainingBalance.compareTo(BigDecimal.ZERO) == 0) {
            this.status = LoanStatus.SETTLED;
        }
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Long getLoanId() { return loanId; }

    public BigDecimal getLoanAmount() { return loanAmount; }

    public Integer getTerm() { return term; }

    public BigDecimal getRemainingBalance() { return remainingBalance; }

    public LoanStatus getStatus() { return status; }

    public void setStatus(LoanStatus status) { this.status = status; }
}
