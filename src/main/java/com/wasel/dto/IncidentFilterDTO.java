package com.wasel.dto;

import com.wasel.model.IncidentCategory;
import com.wasel.model.IncidentSeverity;
import com.wasel.model.IncidentStatus;
import lombok.Data;

@Data
public class IncidentFilterDTO {
    private IncidentCategory category;
    private IncidentSeverity severity;
    private IncidentStatus status;
    private Long checkpointId;
    private Double latitude;
    private Double longitude;
    private Double radius;
    private String sortBy;
    private String sortDirection;
    private int page = 0;
    private int size = 20;
}