package com.wasel.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for authentication requests
 * Contains user credentials for login
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    /**
     * User's email address - used as username
     * Must be a valid email format
     */
    private String email;

    /**
     * User's password - will be encrypted using BCrypt
     * Sent in plain text but never stored without encryption
     */
    private String password;
}