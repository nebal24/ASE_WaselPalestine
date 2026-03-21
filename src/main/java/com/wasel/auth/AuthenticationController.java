package com.wasel.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication endpoints
 * Handles user registration and login requests
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    // Service layer for authentication logic
    private final AuthenticationService authenticationService;

    /**
     * Register a new user
     * Endpoint: POST /api/v1/auth/register
     *
     * @param request contains user details (name, email, password, role)
     * @return JWT token for the newly registered user
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    /**
     * Authenticate existing user (login)
     * Endpoint: POST /api/v1/auth/authenticate
     *
     * @param request contains email and password
     * @return JWT token for authenticated user
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}