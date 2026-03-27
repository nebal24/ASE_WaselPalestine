package com.wasel.dto;

import com.wasel.model.IncidentCategory;
import lombok.Data;

@Data
public class AlertSubscriptionRequest {
    private String placeName;
    private Double radiusKm;
    private IncidentCategory category;
    private Boolean active;
}