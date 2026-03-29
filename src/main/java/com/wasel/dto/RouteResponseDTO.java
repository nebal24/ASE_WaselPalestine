package com.wasel.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * Data Transfer Object for route estimation responses
 * Contains the calculated route information
 */
@Data
@Builder
public class RouteResponseDTO {

    /** Estimated distance in kilometers (rounded to 1 decimal) */
    private Double distanceKm;

    /** Estimated travel time in minutes */
    private Integer durationMinutes;

    /** List of explanatory messages about the route calculation */
    private List<String> metadata;
}