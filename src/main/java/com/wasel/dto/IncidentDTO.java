package com.wasel.dto;

import com.wasel.model.IncidentCategory;
import com.wasel.model.IncidentSeverity;
import com.wasel.model.IncidentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IncidentDTO {
    private Long id;
    private String description;
    private IncidentCategory category;
    private IncidentSeverity severity;
    private Double latitude;
    private Double longitude;
    private IncidentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime verifiedAt;
    private UserSummaryDTO createdBy;
    private UserSummaryDTO verifiedBy;
    private CheckpointSummaryDTO checkpoint;

}