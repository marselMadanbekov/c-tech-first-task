package ctech.firsttask.controllers;

import ctech.firsttask.model.dtos.auth.AuthenticationRequest;
import ctech.firsttask.model.dtos.auth.AuthenticationResponse;
import ctech.firsttask.services.authentication.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authenticationService.logout(authHeader.substring(7));
        }
        return Map.of("message", "Logged out successfully");
    }

    @PostMapping("/login")
    public AuthenticationResponse authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        return authenticationService.login(authenticationRequest);
    }
}
