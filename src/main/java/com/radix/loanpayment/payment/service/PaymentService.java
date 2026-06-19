package com.radix.loanpayment.payment.service;

import com.radix.loanpayment.common.exception.BusinessException;
import com.radix.loanpayment.loan.dto.LoanResponse;
import com.radix.loanpayment.loan.entity.Loan;
import com.radix.loanpayment.loan.entity.LoanStatus;
import com.radix.loanpayment.loan.repository.LoanRepository;
import com.radix.loanpayment.loan.service.LoanService;
import com.radix.loanpayment.payment.dto.CreatePaymentRequest;
import com.radix.loanpayment.payment.dto.PaymentResponse;
import com.radix.loanpayment.payment.entity.Payment;
import com.radix.loanpayment.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for the Payment domain.
 *
 * <p>Handles the business logic for recording payments against loans, including:
 * <ul>
 *   <li>Validating that the loan exists and is still active.</li>
 *   <li>Rejecting overpayments that exceed the remaining balance.</li>
 *   <li>Applying the payment to reduce the loan's remaining balance.</li>
 *   <li>Transitioning the loan to {@code SETTLED} when fully repaid.</li>
 * </ul>
 */
@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;
    private final LoanService loanService;

    public PaymentService(PaymentRepository paymentRepository,
                          LoanRepository loanRepository,
                          LoanService loanService) {
        this.paymentRepository = paymentRepository;
        this.loanRepository = loanRepository;
        this.loanService = loanService;
    }

    /**
     * Records a payment for the specified loan.
     *
     * <p>Business rules enforced:
     * <ol>
     *   <li>The referenced loan must exist.</li>
     *   <li>The loan must be in {@code ACTIVE} status (settled loans cannot receive payments).</li>
     *   <li>The payment amount must not exceed the remaining balance (no overpayments).</li>
     * </ol>
     *
     * @param request the payment request containing loanId and paymentAmount
     * @return a response DTO with the payment record and the updated loan state
     * @throws com.radix.loanpayment.common.exception.ResourceNotFoundException if the loan does not exist
     * @throws BusinessException if the loan is already settled or the payment exceeds the balance
     */
    public PaymentResponse processPayment(CreatePaymentRequest request) {
        // 1. Resolve the loan (throws ResourceNotFoundException if absent)
        Loan loan = loanService.findLoanOrThrow(request.getLoanId());

        // 2. Reject payments on already-settled loans
        if (loan.getStatus() == LoanStatus.SETTLED) {
            throw new BusinessException(
                    "Loan " + loan.getLoanId() + " is already SETTLED and cannot accept further payments.");
        }

        // 3. Reject overpayments
        if (request.getPaymentAmount().compareTo(loan.getRemainingBalance()) > 0) {
            throw new BusinessException(
                    "Payment amount " + request.getPaymentAmount()
                    + " exceeds the remaining balance of " + loan.getRemainingBalance()
                    + " for loan " + loan.getLoanId() + ".");
        }

        // 4. Apply the payment to the loan
        loan.applyPayment(request.getPaymentAmount());
        loanRepository.save(loan);

        // 5. Persist the payment record
        Payment payment = new Payment(loan.getLoanId(), request.getPaymentAmount());
        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.from(savedPayment, LoanResponse.from(loan));
    }
}
