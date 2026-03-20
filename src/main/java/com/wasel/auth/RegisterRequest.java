package com.wasel.auth;

import com.wasel.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for user registration requests
 * Contains all necessary information to create a new user account
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    /**
     * User's full name
     * Required field for account creation
     * Example: "Ahmad Hassan" or "Sarah Mohammed"
     */
    private String name;

    /**
     * User's email address - used as username for login
     * Must be unique in the system
     * Required field
     * Example: "user@example.com"
     */
    private String email;

    /**
     * User's password for account access
     * Will be encrypted using BCrypt before storing in database
     * Required field - minimum 6 characters recommended
     * Never stored or transmitted in plain text
     */
    private String password;

    /**
     * User's role in the system - defines permissions
     * Optional field - defaults to USER if not provided
     * Available roles:
     * - USER: Can create incidents only
     * - MODERATOR: Can create, update, verify, close, delete incidents
     * - ADMIN: Full access to all features
     *
     * Example: Role.USER, Role.MODERATOR, Role.ADMIN
     */
    private Role role;
}