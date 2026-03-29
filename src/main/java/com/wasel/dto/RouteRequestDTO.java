package com.wasel.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO for route estimation request
 * Contains origin, destination, and constraint parameters
 */

@Data
public class RouteRequestDTO {
    // Origin coordinates
    private Double originLat;
    private Double originLon;

    // Destination coordinates
    private Double destinationLat;
    private Double destinationLon;

    // If true, route will avoid all CLOSED or DELAYED checkpoints
    private boolean avoidCheckpoints = false;

    // List of area names to avoid (e.g. "Huwara", "Beita")
    private List<String> avoidAreas;
}
