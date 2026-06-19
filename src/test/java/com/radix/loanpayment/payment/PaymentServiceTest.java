package com.radix.loanpayment.payment;

import com.radix.loanpayment.common.exception.BusinessException;
import com.radix.loanpayment.common.exception.ResourceNotFoundException;
import com.radix.loanpayment.loan.entity.Loan;
import com.radix.loanpayment.loan.entity.LoanStatus;
import com.radix.loanpayment.loan.repository.LoanRepository;
import com.radix.loanpayment.loan.service.LoanService;
import com.radix.loanpayment.payment.dto.CreatePaymentRequest;
import com.radix.loanpayment.payment.dto.PaymentResponse;
import com.radix.loanpayment.payment.entity.Payment;
import com.radix.loanpayment.payment.repository.PaymentRepository;
import com.radix.loanpayment.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PaymentService}.
 *
 * <p>Covers the four key scenarios required by the assessment:
 * <ol>
 *   <li>A payment is successfully recorded and reduces the loan balance.</li>
 *   <li>An overpayment raises a {@link BusinessException}.</li>
 *   <li>A full payment transitions the loan to {@code SETTLED}.</li>
 *   <li>A payment on a non-existent loan raises a {@link ResourceNotFoundException}.</li>
 * </ol>
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private PaymentService paymentService;

    private Loan activeLoan;

    @BeforeEach
    void setUp() {
        activeLoan = new Loan(new BigDecimal("10000.00"), 12);
    }

    // -------------------------------------------------------------------------
    // Successful payment tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("processPayment – should reduce loan balance by payment amount")
    void processPayment_shouldReduceLoanBalance() {
        // Arrange
        when(loanService.findLoanOrThrow(1L)).thenReturn(activeLoan);
        when(loanRepository.save(any(Loan.class))).thenReturn(activeLoan);

        Payment savedPayment = new Payment(1L, new BigDecimal("2000.00"));
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLoanId(1L);
        request.setPaymentAmount(new BigDecimal("2000.00"));

        // Act
        PaymentResponse response = paymentService.processPayment(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getPaymentAmount()).isEqualByComparingTo("2000.00");
        assertThat(response.getUpdatedLoan().getRemainingBalance())
                .isEqualByComparingTo("8000.00");
        assertThat(response.getUpdatedLoan().getStatus()).isEqualTo(LoanStatus.ACTIVE);
        verify(loanRepository, times(1)).save(activeLoan);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("processPayment – should settle the loan when payment equals remaining balance")
    void processPayment_shouldSettleLoanOnFullPayment() {
        // Arrange
        when(loanService.findLoanOrThrow(1L)).thenReturn(activeLoan);
        when(loanRepository.save(any(Loan.class))).thenReturn(activeLoan);

        Payment savedPayment = new Payment(1L, new BigDecimal("10000.00"));
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLoanId(1L);
        request.setPaymentAmount(new BigDecimal("10000.00"));

        // Act
        PaymentResponse response = paymentService.processPayment(request);

        // Assert
        assertThat(response.getUpdatedLoan().getRemainingBalance())
                .isEqualByComparingTo("0.00");
        assertThat(response.getUpdatedLoan().getStatus()).isEqualTo(LoanStatus.SETTLED);
    }

    @Test
    @DisplayName("processPayment – should settle loan after multiple partial payments summing to full amount")
    void processPayment_shouldSettleLoanAfterMultiplePartialPayments() {
        // Arrange – simulate a loan that already has 500 remaining
        Loan partiallyPaidLoan = new Loan(new BigDecimal("1000.00"), 6);
        partiallyPaidLoan.applyPayment(new BigDecimal("500.00")); // remaining = 500

        when(loanService.findLoanOrThrow(2L)).thenReturn(partiallyPaidLoan);
        when(loanRepository.save(any(Loan.class))).thenReturn(partiallyPaidLoan);

        Payment savedPayment = new Payment(2L, new BigDecimal("500.00"));
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLoanId(2L);
        request.setPaymentAmount(new BigDecimal("500.00"));

        // Act
        PaymentResponse response = paymentService.processPayment(request);

        // Assert
        assertThat(response.getUpdatedLoan().getStatus()).isEqualTo(LoanStatus.SETTLED);
        assertThat(response.getUpdatedLoan().getRemainingBalance())
                .isEqualByComparingTo("0.00");
    }

    // -------------------------------------------------------------------------
    // Overpayment tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("processPayment – should throw BusinessException when payment exceeds remaining balance")
    void processPayment_shouldThrowOnOverpayment() {
        // Arrange
        when(loanService.findLoanOrThrow(1L)).thenReturn(activeLoan);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLoanId(1L);
        request.setPaymentAmount(new BigDecimal("15000.00")); // exceeds 10000

        // Act & Assert
        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("exceeds the remaining balance");

        verify(loanRepository, never()).save(any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("processPayment – should throw BusinessException when paying a SETTLED loan")
    void processPayment_shouldThrowWhenLoanAlreadySettled() {
        // Arrange – fully pay the loan first to settle it
        activeLoan.applyPayment(new BigDecimal("10000.00")); // status = SETTLED

        when(loanService.findLoanOrThrow(1L)).thenReturn(activeLoan);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLoanId(1L);
        request.setPaymentAmount(new BigDecimal("100.00"));

        // Act & Assert
        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("SETTLED");

        verify(loanRepository, never()).save(any());
        verify(paymentRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // Non-existent loan tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("processPayment – should propagate ResourceNotFoundException for unknown loanId")
    void processPayment_shouldThrowWhenLoanNotFound() {
        // Arrange
        when(loanService.findLoanOrThrow(999L))
                .thenThrow(new ResourceNotFoundException("Loan not found with id: 999"));

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLoanId(999L);
        request.setPaymentAmount(new BigDecimal("100.00"));

        // Act & Assert
        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
