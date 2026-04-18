package com.wasel.repository;

import com.wasel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
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
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email
     * Used during registration to prevent duplicate emails
     */
    Boolean existsByEmail(String email);

    /**
     * Native query — Query 3: User Statistics by Role
     *
     * Groups all users by their role and aggregates three activity counters:
     *   total_submissions – incidents created by users in that role
     *   total_reports     – crowdsourced reports submitted by users in that role
     *   total_moderations – moderation actions performed by users in that role
     *
     * COUNT(DISTINCT ...) prevents row inflation caused by the three LEFT JOINs
     * fanning out across multiple child rows per user.
     *
     * Used by: Admin analytics dashboard.
     */
    @Query(value = """
            SELECT
                u.role                              AS role,
                COUNT(DISTINCT u.id)               AS user_count,
                -- Incidents created by users of this role
                COUNT(DISTINCT i.incident_id)       AS total_submissions,
                -- Crowd-sourced reports submitted by users of this role
                COUNT(DISTINCT r.report_id)         AS total_reports,
                -- Moderation actions performed by users of this role
                COUNT(DISTINCT ma.action_id)        AS total_moderations
            FROM users u
            LEFT JOIN incidents          i  ON i.created_by   = u.id
            LEFT JOIN reports            r  ON r.user_id       = u.id
            LEFT JOIN moderation_actions ma ON ma.performed_by = u.id
            GROUP BY u.role
            ORDER BY u.role
            """,
            nativeQuery = true)
    List<Object[]> findUserStatsByRole();
}