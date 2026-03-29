package com.wasel.service.impl;

import com.wasel.client.OpenStreetMapClient;
import com.wasel.dto.RouteRequestDTO;
import com.wasel.dto.RouteResponseDTO;
import com.wasel.service.RouteService;
import com.wasel.util.HaversineCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of RouteService
 * Tries external API first, falls back to heuristics if API fails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final OpenStreetMapClient osmClient;
    private final HaversineCalculator haversineCalculator;

    @Override
    public RouteResponseDTO estimateRoute(RouteRequestDTO request) {
        List<String> metadata = new ArrayList<>();

        double distanceKm;
        int durationMinutes;

        // Step 1: Try external API first
        double[] apiResult = osmClient.getRoute(
                request.getStartLng(), request.getStartLat(),
                request.getEndLng(), request.getEndLat()
        );

        if (apiResult != null) {
            // External API succeeded - use real route data
            distanceKm = apiResult[0] / 1000;
            durationMinutes = (int) (apiResult[1] / 60);
            metadata.add("Route calculated using OpenStreetMap API");
            log.info("Used OSRM API: {} km, {} min", distanceKm, durationMinutes);
        } else {
            // External API failed - use heuristics (straight-line distance)
            distanceKm = haversineCalculator.calculateDistance(
                    request.getStartLat(), request.getStartLng(),
                    request.getEndLat(), request.getEndLng()
            );
            durationMinutes = haversineCalculator.estimateDuration(distanceKm, 50);
            metadata.add("Using estimated distance (external API unavailable)");
            log.info("Used fallback: {} km, {} min", distanceKm, durationMinutes);
        }

        // Add additional route information to metadata
        metadata.add(String.format("Distance: %.1f km, Estimated time: %d minutes",
                distanceKm, durationMinutes));
        metadata.add("Route estimated successfully");

        // Build and return response
        return RouteResponseDTO.builder()
                .distanceKm(Math.round(distanceKm * 10) / 10.0)
                .durationMinutes(durationMinutes)
                .metadata(metadata)
                .build();
    }
}