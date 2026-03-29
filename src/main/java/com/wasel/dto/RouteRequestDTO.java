package com.wasel.dto;

import lombok.Data;

/**
 * Data Transfer Object for route estimation requests
 * Contains the start and end coordinates for route calculation
 */
@Data
public class RouteRequestDTO {

    /** Starting point latitude */
    private Double startLat;

    /** Starting point longitude  */
    private Double startLng;

    /** Destination latitude */
    private Double endLat;

    /** Destination longitude */
    private Double endLng;

    /** Optional: whether to avoid checkpoints on the route */
    private boolean avoidCheckpoints;
}