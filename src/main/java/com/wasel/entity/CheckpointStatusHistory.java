package com.wasel.entity;

import com.wasel.model.CheckpointStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "checkpoint_status_history")
@Data
public class CheckpointStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId;

    @ManyToOne
    @JoinColumn(name = "checkpoint_id", nullable = false)
    private CheckPoint checkpoint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckpointStatus status;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}