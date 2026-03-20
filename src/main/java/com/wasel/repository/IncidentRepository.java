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

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    Page<Incident> findByCategory(IncidentCategory category, Pageable pageable);

    Page<Incident> findBySeverity(IncidentSeverity severity, Pageable pageable);

    Page<Incident> findByStatus(IncidentStatus status, Pageable pageable);

    Page<Incident> findByCheckpointId(Long checkpointId, Pageable pageable);

    Page<Incident> findByCreatedById(Long userId, Pageable pageable);

    Page<Incident> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

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