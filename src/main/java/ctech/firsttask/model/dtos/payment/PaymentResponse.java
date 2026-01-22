package ctech.firsttask.model.dtos.payment;

import java.math.BigDecimal;

public record PaymentResponse(
        String message,
        BigDecimal amountDeducted,
        BigDecimal newBalance
) {}