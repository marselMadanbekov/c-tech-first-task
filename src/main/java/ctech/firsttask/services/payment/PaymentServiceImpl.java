package ctech.firsttask.services.payment;

import ctech.firsttask.exceptions.InsufficientFundsException;
import ctech.firsttask.model.entities.Payment;
import ctech.firsttask.model.entities.User;
import ctech.firsttask.repositories.PaymentRepository;
import ctech.firsttask.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal PAYMENT_AMOUNT = new BigDecimal("1.10");

    private final UserRepository userRepository;

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public BigDecimal processPayment(String username) {
        User user = userRepository.findByUsernameForUpdate(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getBalance().compareTo(PAYMENT_AMOUNT) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Current balance: " + user.getBalance());
        }

        // Deduct balance
        user.setBalance(user.getBalance().subtract(PAYMENT_AMOUNT));
        userRepository.save(user);

        // Record payment
        Payment payment = Payment.builder()
                .user(user)
                .amount(PAYMENT_AMOUNT)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        return user.getBalance();
    }
}
