package com.wasel.controller;

import com.wasel.dto.RouteRequestDTO;
import com.wasel.dto.RouteResponseDTO;
import com.wasel.service.RouteMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

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
    private final RestTemplate restTemplate = new RestTemplate();

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
        if (originLat == null || originLon == null || destinationLat == null || destinationLon == null) {
            return ResponseEntity.badRequest().build();
        }
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

        double baseDistance = 0.0;
        double baseDuration = 0.0;

        // Call OSRM service to get base distance/duration
        try {
            String url = String.format(
                    "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false",
                    originLon, originLat, destinationLon, destinationLat);

            @SuppressWarnings("rawtypes")
            java.util.Map osrmResponse = restTemplate.getForObject(url, java.util.Map.class);
            if (osrmResponse != null && osrmResponse.containsKey("routes")) {
                Object routesObj = osrmResponse.get("routes");
                if (routesObj instanceof java.util.List && !((java.util.List) routesObj).isEmpty()) {
                    Object first = ((java.util.List) routesObj).get(0);
                    if (first instanceof java.util.Map) {
                        java.util.Map firstRoute = (java.util.Map) first;
                        Object dist = firstRoute.get("distance");
                        Object dur = firstRoute.get("duration");
                        if (dist instanceof Number) {
                            baseDistance = ((Number) dist).doubleValue() / 1000.0; // meters -> km
                        }
                        if (dur instanceof Number) {
                            baseDuration = ((Number) dur).doubleValue() / 60.0; // seconds -> minutes
                        }
                    }
                }
            }
        } catch (RestClientException ex) {
            // fallback to Haversine if OSRM fails
            baseDistance = haversineDistance(originLat, originLon, destinationLat, destinationLon);
            // rough duration: assume average speed 40 km/h => minutes = (km / 40) * 60
            baseDuration = (baseDistance / 40.0) * 60.0;
        }

        // Person 2: Build full response with metadata
        RouteResponseDTO response = routeMetadataService.buildRouteWithMetadata(
                request, baseDistance, baseDuration
        );

        return ResponseEntity.ok(response);
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}