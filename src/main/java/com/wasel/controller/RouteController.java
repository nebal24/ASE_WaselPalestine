package com.wasel.controller;

import com.wasel.dto.RouteRequestDTO;
import com.wasel.dto.RouteResponseDTO;
import com.wasel.service.RouteMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for route estimation endpoints
 * Base path: /api/v1/routes
 *
 * Person 1 responsibility: getRouteEstimation base (distance + duration)
 * Person 2 responsibility: metadata enrichment via RouteMetadataService
 */
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteMetadataService routeMetadataService;

    /**
     * Estimate a route between two locations with metadata.
     *
     * GET /api/v1/routes/estimate
     *
     * Query params:
     *   originLat, originLon         — starting point
     *   destinationLat, destinationLon — ending point
     *   avoidCheckpoints (optional)  — true/false
     *   avoidAreas (optional)        — comma-separated area names
     *
     * Example:
     * GET /api/v1/routes/estimate?originLat=32.22&originLon=35.25
     *      &destinationLat=32.10&destinationLon=35.20
     *      &avoidCheckpoints=true
     *      &avoidAreas=Huwara,Beita
     */
    @GetMapping("/estimate")
    public ResponseEntity<RouteResponseDTO> estimateRoute(
            @RequestParam Double originLat,
            @RequestParam Double originLon,
            @RequestParam Double destinationLat,
            @RequestParam Double destinationLon,
            @RequestParam(defaultValue = "false") boolean avoidCheckpoints,
            @RequestParam(required = false) java.util.List<String> avoidAreas) {

        // Validate coordinates
        if (originLat < -90 || originLat > 90 || destinationLat < -90 || destinationLat > 90 ||
            originLon < -180 || originLon > 180 || destinationLon < -180 || destinationLon > 180) {
            return ResponseEntity.badRequest().build();
        }

        // Build request DTO from query params
        RouteRequestDTO request = new RouteRequestDTO();
        request.setOriginLat(originLat);
        request.setOriginLon(originLon);
        request.setDestinationLat(destinationLat);
        request.setDestinationLon(destinationLon);
        request.setAvoidCheckpoints(avoidCheckpoints);
        request.setAvoidAreas(avoidAreas);

        // TODO (Person 1): Replace these hardcoded values with actual Routing API call
        // These are placeholder values until Person 1 integrates the external Routing API
        double baseDistance = 0.0;
        double baseDuration = 0.0;

        // Person 2: Build full response with metadata
        RouteResponseDTO response = routeMetadataService.buildRouteWithMetadata(
                request, baseDistance, baseDuration
        );

        return ResponseEntity.ok(response);
    }
}