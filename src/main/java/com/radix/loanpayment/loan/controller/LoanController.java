package com.radix.loanpayment.loan.controller;

import com.radix.loanpayment.loan.dto.CreateLoanRequest;
import com.radix.loanpayment.loan.dto.LoanResponse;
import com.radix.loanpayment.loan.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller for the Loan domain.
 *
 * <p>Exposes two endpoints:
 * <ul>
 *   <li>{@code POST /loans}          – create a new loan.</li>
 *   <li>{@code GET  /loans/{loanId}} – retrieve a loan by ID.</li>
 * </ul>
 */
@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Creates a new loan.
     *
     * @param request the loan creation payload (loanAmount, term)
     * @return {@code 201 Created} with the created loan details
     */
    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody CreateLoanRequest request) {
        LoanResponse response = loanService.createLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a loan by its ID.
     *
     * @param loanId the unique identifier of the loan
     * @return {@code 200 OK} with the loan details, or {@code 404 Not Found}
     */
    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable Long loanId) {
        LoanResponse response = loanService.getLoanById(loanId);
        return ResponseEntity.ok(response);
    }
}
