package com.wasel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.wasel.model.IncidentCategory;
import com.wasel.model.IncidentSeverity;
import com.wasel.model.IncidentStatus;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Entity representing a traffic incident in the system
 * Maps to the 'incidents' table in database
 */
@Entity
@Table(name = "incidents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incidentId;

    /**
     * Detailed description of the incident
     */
    @Column(nullable = false, length = 1000)
    private String description;

    /**
     * Type/category of the incident
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentCategory category;

    /**
     * Severity level of the incident
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentSeverity severity;

    /**
     * Latitude coordinate of incident location
     */
    @Column(nullable = false)
    private Double latitude;

    /**
     * Longitude coordinate of incident location
     */
    @Column(nullable = false)
    private Double longitude;

    /**
     * Current status of the incident
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    /**
     * Timestamp when incident was created
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when incident was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Timestamp when incident was verified
     */
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    /**
     * User who created the incident
     */
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /**
     * User (moderator/admin) who verified the incident
     */
    @ManyToOne
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    /**
     * Checkpoint associated with this incident
     */
    @ManyToOne
    @JoinColumn(name = "checkpoint_id")
    private Checkpoint checkpoint;

    /**
     * Sets default values before entity is persisted
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = IncidentStatus.OPEN;
        }
    }

    /**
     * Updates timestamps before entity is updated
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == IncidentStatus.VERIFIED && verifiedAt == null) {
            verifiedAt = LocalDateTime.now();
        }
    }
}