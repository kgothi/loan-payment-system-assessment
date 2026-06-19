package com.radix.loanpayment.payment.controller;

import com.radix.loanpayment.payment.dto.CreatePaymentRequest;
import com.radix.loanpayment.payment.dto.PaymentResponse;
import com.radix.loanpayment.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller for the Payment domain.
 *
 * <p>Exposes one endpoint:
 * <ul>
 *   <li>{@code POST /payments} – record a payment against an existing loan.</li>
 * </ul>
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Records a payment for a loan.
     *
     * @param request the payment payload (loanId, paymentAmount)
     * @return {@code 201 Created} with the payment record and updated loan state,
     *         or {@code 400 Bad Request} if business rules are violated
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
