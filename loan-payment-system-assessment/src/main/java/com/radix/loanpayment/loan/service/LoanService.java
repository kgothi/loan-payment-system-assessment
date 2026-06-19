package com.radix.loanpayment.loan.service;

import com.radix.loanpayment.common.exception.ResourceNotFoundException;
import com.radix.loanpayment.loan.dto.CreateLoanRequest;
import com.radix.loanpayment.loan.dto.LoanResponse;
import com.radix.loanpayment.loan.entity.Loan;
import com.radix.loanpayment.loan.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for the Loan domain.
 *
 * <p>Encapsulates all business logic related to loan creation and retrieval,
 * keeping the controller thin and the domain logic testable in isolation.
 */
@Service
@Transactional
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Creates a new loan from the supplied request and persists it.
     *
     * @param request the loan creation request containing amount and term
     * @return a response DTO representing the newly created loan
     */
    public LoanResponse createLoan(CreateLoanRequest request) {
        Loan loan = new Loan(request.getLoanAmount(), request.getTerm());
        Loan saved = loanRepository.save(loan);
        return LoanResponse.from(saved);
    }

    /**
     * Retrieves a loan by its unique identifier.
     *
     * @param loanId the ID of the loan to retrieve
     * @return a response DTO for the found loan
     * @throws ResourceNotFoundException if no loan with the given ID exists
     */
    @Transactional(readOnly = true)
    public LoanResponse getLoanById(Long loanId) {
        Loan loan = findLoanOrThrow(loanId);
        return LoanResponse.from(loan);
    }

    /**
     * Finds a {@link Loan} entity by ID, throwing if absent.
     * This method is package-accessible so the Payment service can reuse it
     * without exposing the repository outside the loan package.
     *
     * @param loanId the loan ID to look up
     * @return the found {@link Loan} entity
     * @throws ResourceNotFoundException if no loan exists with the given ID
     */
    public Loan findLoanOrThrow(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loan not found with id: " + loanId));
    }
}
