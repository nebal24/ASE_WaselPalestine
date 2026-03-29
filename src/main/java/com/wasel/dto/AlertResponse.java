package com.wasel.dto;

import com.wasel.model.AlertStatus;
import com.wasel.model.IncidentCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponse {

    private Long id;
    private Long incidentId;
    private IncidentCategory incidentCategory;
    private Double incidentLatitude;
    private Double incidentLongitude;
    private AlertStatus status;
    private LocalDateTime createdAt;
    private WeatherResponse weather;
}