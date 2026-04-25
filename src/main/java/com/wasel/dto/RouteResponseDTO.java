package com.wasel.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * DTO for route estimation response
 * Contains distance, duration, and explanatory metadata
 */
@Data
@Builder
public class RouteResponseDTO {

    // Estimated distance in kilometers
    private Double estimatedDistance;

    // Estimated duration in minutes
    private Double estimatedDuration;

    // Metadata explaining factors that affect this route
    private RouteMetadata metadata;


    @Data
    @Builder
    public static class RouteMetadata {

        // Human-readable list of factors affecting the route
        // e.g. ["Checkpoint CLOSED on route", "Active incident near destination"]
        private List<String> factors;

        // Checkpoints that are closed or delayed along the route
        private List<AffectedCheckpoint> affectedCheckpoints;

        // Areas that were requested to be avoided
        private List<String> avoidedAreas;

        // Whether the route was modified due to constraints
        private boolean routeModified;
    }

    @Data
    @Builder
    public static class AffectedCheckpoint {
        private Long id;
        private String name;
        private String status;
        private Double latitude;
        private Double longitude;
    }
}