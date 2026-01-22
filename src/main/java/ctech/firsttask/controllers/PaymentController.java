package ctech.firsttask.controllers;

import ctech.firsttask.model.dtos.payment.PaymentResponse;
import ctech.firsttask.services.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/payment")
    public PaymentResponse makePayment(Authentication authentication) {

        String username = authentication.getName();

        BigDecimal newBalance = paymentService.processPayment(username);

        log.info("Payment request received for username {}, new balance {}", username, newBalance);
        return new PaymentResponse(
                "Payment successful",
                new BigDecimal("1.10"),
                newBalance
        );
    }
}
