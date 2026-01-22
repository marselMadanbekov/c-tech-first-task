package ctech.firsttask.services.authentication;

import ctech.firsttask.model.dtos.auth.AuthenticationRequest;
import ctech.firsttask.model.dtos.auth.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse login(AuthenticationRequest request);

    void logout(String token);
}
