package com.wasel.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wasel.model.CheckpointStatus;
import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(name = "checkpoint_status_history")
@Data
public class CheckpointStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "checkpoint_id", nullable = false)
    private Checkpoint checkpoint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckpointStatus status;

    private LocalDateTime updatedAt;

    @JsonIgnoreProperties({"votes", "moderationActions", "authorities",
            "accountNonExpired", "accountNonLocked",
            "credentialsNonExpired", "enabled", "username"})
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}