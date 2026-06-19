package com.radix.loanpayment.payment.repository;

import com.radix.loanpayment.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Payment} entities.
 *
 * <p>Provides standard CRUD operations and a convenience finder
 * for retrieving all payments associated with a specific loan.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Finds all payments made towards a specific loan.
     *
     * @param loanId the loan ID to filter by
     * @return a list of payments for the given loan (may be empty)
     */
    List<Payment> findByLoanId(Long loanId);
}
