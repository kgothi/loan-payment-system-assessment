package com.radix.loanpayment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test that verifies the Spring application context loads successfully.
 */
@SpringBootTest
class LoanPaymentApplicationTest {

    @Test
    @DisplayName("Application context should load without errors")
    void contextLoads() {
        // If the context fails to load, this test will fail automatically.
    }
}
