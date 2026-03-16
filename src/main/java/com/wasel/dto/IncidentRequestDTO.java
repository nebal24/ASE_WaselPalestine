package com.wasel.dto;
import lombok.Data;
import com.wasel.model.IncidentCategory;
import com.wasel.model.IncidentSeverity;
@Data
public class IncidentRequestDTO {
    private String description;
    private IncidentCategory category;
    private IncidentSeverity severity;
    private Double latitude;
    private Double longitude;
    private Long checkpointId;
}