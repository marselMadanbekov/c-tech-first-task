package ctech.firsttask.services.authentication;

import ctech.firsttask.model.dtos.auth.AuthenticationRequest;
import ctech.firsttask.model.dtos.auth.AuthenticationResponse;
import ctech.firsttask.model.entities.BlacklistedToken;
import ctech.firsttask.model.entities.User;
import ctech.firsttask.repositories.BlacklistedTokenRepository;
import ctech.firsttask.repositories.UserRepository;
import ctech.firsttask.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 1; // 3 minutes
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;


    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!user.isAccountNonLocked()) {
            throw new LockedException("Account is locked. Try again later.");
        }

        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            resetFailedAttempts(user);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtService.generateToken(user);
            return new AuthenticationResponse(token);

        } catch (AuthenticationException ex) {
            increaseFailedAttempts(user);
            throw ex;
        }
    }

    @Override
    public void logout(String token) {
        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        blacklistedTokenRepository.save(blacklistedToken);
    }

    private void increaseFailedAttempts(User user) {
        user.setFailedAttempts(user.getFailedAttempts() + 1);
        if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
            log.info("User {} locked, failed attempts {}", user.getUsername(), user.getFailedAttempts());
            user.setLockTime(LocalDateTime.now().plusMinutes(LOCK_TIME_DURATION));
            user.setFailedAttempts(0);
        }
        userRepository.saveAndFlush(user);
    }

    private void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        user.setLockTime(null);
        userRepository.saveAndFlush(user);
    }
}