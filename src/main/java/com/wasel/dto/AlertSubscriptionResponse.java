package com.wasel.dto;

import com.wasel.model.IncidentCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertSubscriptionResponse {
    private Long id;
    private Long userId;
    private String placeName;
    private Double centerLatitude;
    private Double centerLongitude;
    private Double radiusKm;
    private IncidentCategory category;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}