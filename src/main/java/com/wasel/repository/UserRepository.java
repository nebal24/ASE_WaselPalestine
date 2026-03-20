package com.wasel.repository;

import com.wasel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 * Provides methods for user data access and authentication
 * Extends JpaRepository to inherit standard CRUD operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their email address
     * Used during authentication to load user details
     * Email is unique so this returns at most one user
     *
     * @param email the email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email
     * Used during registration to prevent duplicate emails
     *
     * @param email the email address to check
     * @return true if a user with this email already exists, false otherwise
     */
    Boolean existsByEmail(String email);
}