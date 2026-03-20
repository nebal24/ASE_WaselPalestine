package com.wasel.entity;

import com.wasel.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entity representing a user in the system
 * Implements UserDetails for Spring Security integration
 * Maps to 'users' table in database
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's full name
     */
    @Column(nullable = false)
    private String name;

    /**
     * User's email address - used as username for login
     * Must be unique across all users
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Encrypted password (BCrypt hash)
     * Never stored as plain text
     */
    @Column(nullable = false)
    private String password;

    /**
     * User's role determining permissions
     * Possible values: USER, MODERATOR, ADMIN
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Account creation timestamp
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Account status flag
     * Defaults to true (active account)
     */
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Returns authorities granted to the user
     * Converts role to Spring Security authority format (ROLE_*)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Returns the email used as username for authentication
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indicates whether the user's account has expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials have expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled
     * Returns the isActive flag value
     */
    @Override
    public boolean isEnabled() {
        return isActive;
    }
}