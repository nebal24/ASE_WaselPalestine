package com.wasel.auth;

import com.wasel.entity.User;
import com.wasel.model.Role;
import com.wasel.repository.UserRepository;
import com.wasel.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service class for handling authentication operations
 * Manages user registration and login logic
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    // Repository for user data operations
    private final UserRepository userRepository;

    // Encoder for secure password hashing (BCrypt)
    private final PasswordEncoder passwordEncoder;

    // Service for JWT token generation and validation
    private final JwtService jwtService;

    // Spring's authentication manager for validating credentials
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user in the system
     *
     * @param request contains user details (name, email, password, role)
     * @return AuthenticationResponse containing JWT token
     *
     * Process:
     * 1. Create new User object from request
     * 2. Encode the password using BCrypt
     * 3. Set default role to USER if none provided
     * 4. Save user to database
     * 5. Generate JWT token for the new user
     */
    public AuthenticationResponse register(RegisterRequest request) {
        // Build user entity with encoded password
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hash password
                .role(request.getRole() != null ? request.getRole() : Role.USER) // Default role
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .build();

        // Save user to database
        userRepository.save(user);

        // Generate JWT token for the new user
        var jwtToken = jwtService.generateToken(user);

        // Return token in response
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Authenticate existing user (login)
     *
     * @param request contains email and password
     * @return AuthenticationResponse containing JWT token
     *
     * Process:
     * 1. Validate credentials using AuthenticationManager
     * 2. If valid, fetch user from database
     * 3. Generate new JWT token
     * 4. Return token to client
     *
     * @throws RuntimeException if authentication fails or user not found
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Validate username and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Fetch user from database
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT token
        var jwtToken = jwtService.generateToken(user);

        // Return token in response
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}