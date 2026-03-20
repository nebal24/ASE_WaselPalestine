package com.wasel.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for authentication responses
 * Contains JWT token returned after successful login/registration
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    /**
     * JWT (JSON Web Token) generated after successful authentication
     * Client must include this token in Authorization header for subsequent requests
     * Format: Bearer <token>
     *
     * Example:
     * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
     *
     * Token contains:
     * - User's email (subject)
     * - Issued at timestamp
     * - Expiration time (24 hours)
     * - Digital signature to prevent tampering
     */
    private String token;
}