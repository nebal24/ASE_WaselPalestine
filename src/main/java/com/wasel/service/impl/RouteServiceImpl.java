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
        double durationMinutes;

        // Step 1: Try external API first
        double[] apiResult = osmClient.getRoute(
                request.getOriginLon(), request.getOriginLat(),
                request.getDestinationLon(), request.getDestinationLat()
        );

        if (apiResult != null) {
            // External API succeeded - use real route data
            distanceKm = apiResult[0] / 1000.0;
            durationMinutes = (apiResult[1] / 60.0);
            metadata.add("Route calculated using OSRM API");
            log.info("Used OSRM API: {} km, {} min", distanceKm, durationMinutes);
        } else {
            // External API failed - use heuristics (straight-line distance)
            distanceKm = haversineCalculator.calculateDistance(
                    request.getOriginLat(), request.getOriginLon(),
                    request.getDestinationLat(), request.getDestinationLon()
            );
            durationMinutes = haversineCalculator.estimateDuration(distanceKm, 50);
            metadata.add("Using estimated distance (external API unavailable)");
            log.info("Used fallback: {} km, {} min", distanceKm, durationMinutes);
        }

        // Add additional route information to metadata
        metadata.add(String.format("Distance: %.1f km, Estimated time: %.0f minutes",
                distanceKm, durationMinutes));
        metadata.add("Route estimated successfully");

        // Build metadata DTO
        RouteResponseDTO.RouteMetadata meta = RouteResponseDTO.RouteMetadata.builder()
                .factors(metadata)
                .affectedCheckpoints(null)
                .avoidedAreas(null)
                .routeModified(false)
                .build();

        // Round distance to 1 decimal
        double roundedDistance = Math.round(distanceKm * 10.0) / 10.0;

        // Build and return response
        return RouteResponseDTO.builder()
                .estimatedDistance(Double.valueOf(roundedDistance))
                .estimatedDuration(Double.valueOf(durationMinutes))
                .metadata(meta)
                .build();
    }
}