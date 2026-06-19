package com.radix.loanpayment.loan.repository;

import com.radix.loanpayment.loan.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Loan} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository}.
 * Custom query methods can be added here as the domain grows.
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
}
