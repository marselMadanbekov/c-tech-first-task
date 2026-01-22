package ctech.firsttask.model.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationRequest {
    @NotNull(message = "Username is required")
    @NotBlank(message = "Username is required!")
    @Size(min = 4, message = "Login must be longer than 4 chars")
    private String username;
    @NotNull(message = "Password is required")
    @NotBlank(message = "Password is required!")
    @Size(min = 4, message = "Password must be longer than 4 chars")
    private String password;
}
