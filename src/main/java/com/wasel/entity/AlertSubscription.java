package com.wasel.entity;

import com.wasel.model.IncidentCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Owner of the subscription
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Optional friendly area label, e.g. "Nablus"
     */
    @Column(name = "place_name", length = 255)
    private String placeName;

    /**
     * Center point of the subscribed geographic area
     */
    @Column(name = "center_latitude", nullable = false)
    private Double centerLatitude;

    @Column(name = "center_longitude", nullable = false)
    private Double centerLongitude;

    /**
     * Radius in kilometers
     */
    @Column(name = "radius_km", nullable = false)
    private Double radiusKm;

    /**
     * Incident category the user wants alerts for
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentCategory category;

    /**
     * Active / inactive subscription
     */
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (active == null) {
            active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}