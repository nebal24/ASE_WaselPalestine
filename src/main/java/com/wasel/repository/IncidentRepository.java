package com.wasel.repository;

import com.wasel.entity.Incident;
import com.wasel.model.IncidentCategory;
import com.wasel.model.IncidentSeverity;
import com.wasel.model.IncidentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

/**
 * Repository interface for Incident entity operations
 * Provides CRUD operations and custom query methods for incidents
 * Extends JpaRepository to inherit standard data access methods
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    /**
     * Find all incidents by category with pagination
     *
     * @param category the incident category to filter by
     * @param pageable pagination information
     * @return page of incidents matching the category
     */
    Page<Incident> findByCategory(IncidentCategory category, Pageable pageable);

    /**
     * Find all incidents by severity with pagination
     *
     * @param severity the incident severity to filter by
     * @param pageable pagination information
     * @return page of incidents matching the severity
     */
    Page<Incident> findBySeverity(IncidentSeverity severity, Pageable pageable);

    /**
     * Find all incidents by status with pagination
     *
     * @param status the incident status to filter by (OPEN, VERIFIED, CLOSED)
     * @param pageable pagination information
     * @return page of incidents matching the status
     */
    Page<Incident> findByStatus(IncidentStatus status, Pageable pageable);

    /**
     * Find all incidents associated with a specific checkpoint
     *
     * @param checkpointId the ID of the checkpoint
     * @param pageable pagination information
     * @return page of incidents at the specified checkpoint
     */
    Page<Incident> findByCheckpointId(Long checkpointId, Pageable pageable);

    /**
     * Find all incidents created by a specific user
     *
     * @param userId the ID of the user who created the incidents
     * @param pageable pagination information
     * @return page of incidents created by the user
     */
    Page<Incident> findByCreatedById(Long userId, Pageable pageable);

    /**
     * Find all incidents created within a specific time range
     *
     * @param start start of the time range
     * @param end end of the time range
     * @param pageable pagination information
     * @return page of incidents created between start and end
     */
    Page<Incident> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * Advanced query for filtering incidents with multiple optional parameters
     * All parameters are optional - if null, that filter is ignored
     *
     * @param category incident category (optional)
     * @param severity incident severity (optional)
     * @param status incident status (optional)
     * @param checkpointId checkpoint ID (optional)
     * @param latitude latitude for radius search (optional - currently not implemented)
     * @param longitude longitude for radius search (optional - currently not implemented)
     * @param radius search radius in km (optional - currently not implemented)
     * @param pageable pagination and sorting information
     * @return page of incidents matching all provided filters
     */
    @Query("SELECT i FROM Incident i WHERE " +
            "(:category IS NULL OR i.category = :category) AND " +
            "(:severity IS NULL OR i.severity = :severity) AND " +
            "(:status IS NULL OR i.status = :status) AND " +
            "(:checkpointId IS NULL OR i.checkpoint.id = :checkpointId)")
    Page<Incident> findWithFilters(
            @Param("category") IncidentCategory category,
            @Param("severity") IncidentSeverity severity,
            @Param("status") IncidentStatus status,
            @Param("checkpointId") Long checkpointId,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            Pageable pageable);
}