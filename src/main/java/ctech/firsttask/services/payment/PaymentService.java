package ctech.firsttask.services.payment;

import java.math.BigDecimal;

public interface PaymentService {
    BigDecimal processPayment(String username);
}
