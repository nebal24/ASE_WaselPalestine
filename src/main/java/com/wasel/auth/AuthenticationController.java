package com.wasel.auth;

import com.wasel.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication endpoints
 * Handles user registration, login, and token refresh
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Register a new user
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    /**
     * Authenticate existing user (login)
     * POST /api/v1/auth/authenticate
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    /**
     * Exchange a valid refresh token for a new access token.
     * POST /api/v1/auth/refresh-token
     * Header: Authorization: Bearer <refresh_token>
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token missing"));
        }

        String token = authHeader.substring(7);

        try {
            // Reject access tokens presented here — only refresh tokens are accepted
            if (!jwtService.isRefreshToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Provided token is not a refresh token"));
            }

            String userEmail = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (!jwtService.isTokenValid(token, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Refresh token is expired or invalid"));
            }

            String newAccessToken = jwtService.generateAccessToken(userDetails);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid refresh token"));
        }
    }
}