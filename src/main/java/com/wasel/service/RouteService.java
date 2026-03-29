package com.wasel.service;

import com.wasel.dto.RouteRequestDTO;
import com.wasel.dto.RouteResponseDTO;

/**
 * Service interface for route estimation operations
 */
public interface RouteService {

    /**
     * Calculate route between two points
     *
     * @param request Contains start and end coordinates
     * @return Route response with distance, duration, and metadata
     */
    RouteResponseDTO estimateRoute(RouteRequestDTO request);
}