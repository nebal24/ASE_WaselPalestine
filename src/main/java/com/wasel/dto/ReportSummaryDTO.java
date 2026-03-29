package com.wasel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wasel.model.IncidentCategory;
import com.wasel.model.ReportStatus;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportSummaryDTO {

    private Long reportId;
    private IncidentCategory category;
    private String description;
    private Double latitude;
    private Double longitude;
    private ReportStatus status;
    private LocalDateTime timestamp;

    // Constructor
    public ReportSummaryDTO(Long reportId, IncidentCategory category, String description,
                            Double latitude, Double longitude,
                            ReportStatus status, LocalDateTime timestamp) {
        this.reportId = reportId;
        this.category = category;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters
    public Long getReportId()          { return reportId; }
    public IncidentCategory getCategory()      { return category; }
    public String getDescription()     { return description; }
    public Double getLatitude()        { return latitude; }
    public Double getLongitude()       { return longitude; }
    public ReportStatus getStatus()    { return status; }
    public LocalDateTime getTimestamp(){ return timestamp; }
}