package com.radix.loanpayment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Loan Payment System application.
 *
 * <p>This Spring Boot application manages two core domains:
 * <ul>
 *   <li><strong>Loan Domain</strong> – handles loan creation and retrieval.</li>
 *   <li><strong>Payment Domain</strong> – handles payments made towards loans.</li>
 * </ul>
 */
@SpringBootApplication
public class LoanPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanPaymentApplication.class, args);
    }
}
