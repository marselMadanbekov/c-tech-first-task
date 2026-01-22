package ctech.firsttask;

import ctech.firsttask.model.entities.User;
import ctech.firsttask.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("testuser").isEmpty()) {
            User user = User.builder()
                    .username("testuser")
                    .password(passwordEncoder.encode("password123"))
                    .balance(new BigDecimal("8.00"))
                    .failedAttempts(0)
                    .build();
            userRepository.save(user);
            log.info("Test user created: testuser / password123");
        }
    }
}