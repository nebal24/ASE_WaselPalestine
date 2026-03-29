package com.wasel.entity;

import com.wasel.model.AlertStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who will receive this alert
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The incident that triggered this alert
    @ManyToOne
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    // PENDING → alert created, not yet sent
    // SENT    → external service picked it up and sent it
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status = AlertStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}