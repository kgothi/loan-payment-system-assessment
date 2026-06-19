package com.radix.loanpayment.loan;

import com.radix.loanpayment.common.exception.ResourceNotFoundException;
import com.radix.loanpayment.loan.dto.CreateLoanRequest;
import com.radix.loanpayment.loan.dto.LoanResponse;
import com.radix.loanpayment.loan.entity.Loan;
import com.radix.loanpayment.loan.entity.LoanStatus;
import com.radix.loanpayment.loan.repository.LoanRepository;
import com.radix.loanpayment.loan.service.LoanService;
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
 * Unit tests for {@link LoanService}.
 *
 * <p>Uses Mockito to isolate the service from the database layer, ensuring that
 * business logic is verified independently of persistence infrastructure.
 */
@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private CreateLoanRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CreateLoanRequest();
        validRequest.setLoanAmount(new BigDecimal("10000.00"));
        validRequest.setTerm(12);
    }

    // -------------------------------------------------------------------------
    // createLoan tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("createLoan – should create and return a loan with ACTIVE status")
    void createLoan_shouldReturnActiveLoan() {
        // Arrange
        Loan savedLoan = new Loan(new BigDecimal("10000.00"), 12);
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        // Act
        LoanResponse response = loanService.createLoan(validRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getLoanAmount()).isEqualByComparingTo("10000.00");
        assertThat(response.getTerm()).isEqualTo(12);
        assertThat(response.getRemainingBalance()).isEqualByComparingTo("10000.00");
        assertThat(response.getStatus()).isEqualTo(LoanStatus.ACTIVE);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    @DisplayName("createLoan – remaining balance should equal loan amount on creation")
    void createLoan_remainingBalanceShouldEqualLoanAmount() {
        // Arrange
        Loan savedLoan = new Loan(new BigDecimal("5000.00"), 6);
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        CreateLoanRequest request = new CreateLoanRequest();
        request.setLoanAmount(new BigDecimal("5000.00"));
        request.setTerm(6);

        // Act
        LoanResponse response = loanService.createLoan(request);

        // Assert
        assertThat(response.getRemainingBalance())
                .isEqualByComparingTo(response.getLoanAmount());
    }

    // -------------------------------------------------------------------------
    // getLoanById tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getLoanById – should return loan when it exists")
    void getLoanById_shouldReturnLoanWhenFound() {
        // Arrange
        Loan loan = new Loan(new BigDecimal("10000.00"), 12);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        // Act
        LoanResponse response = loanService.getLoanById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getLoanAmount()).isEqualByComparingTo("10000.00");
    }

    @Test
    @DisplayName("getLoanById – should throw ResourceNotFoundException when loan does not exist")
    void getLoanById_shouldThrowWhenNotFound() {
        // Arrange
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> loanService.getLoanById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // -------------------------------------------------------------------------
    // findLoanOrThrow tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findLoanOrThrow – should return entity when loan exists")
    void findLoanOrThrow_shouldReturnEntityWhenFound() {
        // Arrange
        Loan loan = new Loan(new BigDecimal("2000.00"), 3);
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));

        // Act
        Loan result = loanService.findLoanOrThrow(5L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getLoanAmount()).isEqualByComparingTo("2000.00");
    }

    @Test
    @DisplayName("findLoanOrThrow – should throw ResourceNotFoundException for unknown ID")
    void findLoanOrThrow_shouldThrowForUnknownId() {
        // Arrange
        when(loanRepository.findById(42L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> loanService.findLoanOrThrow(42L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("42");
    }
}
