package com.wasel.service.impl;

import com.wasel.client.OpenStreetMapClient;
import com.wasel.dto.RouteRequestDTO;
import com.wasel.dto.RouteResponseDTO;
import com.wasel.entity.Checkpoint;
import com.wasel.model.CheckpointStatus;
import com.wasel.repository.CheckpointRepository;
import com.wasel.service.GeocodingService;
import com.wasel.service.RouteService;
import com.wasel.util.HaversineCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final OpenStreetMapClient osmClient;
    private final HaversineCalculator haversineCalculator;
    private final CheckpointRepository checkpointRepository;
    private final GeocodingService geocodingService;

    /** Checkpoints within this many km of the route corridor are considered "on route". */
    private static final double CORRIDOR_RADIUS_KM = 3.0;

    /** Areas within this many km of the route corridor are considered "on route". */
    private static final double AREA_RADIUS_KM = 5.0;

    @Override
    public RouteResponseDTO estimateRoute(RouteRequestDTO request) {
        List<String> factors = new ArrayList<>();
        boolean routeModified = false;

        // Step 1: obtain distance/duration from OSRM, fall back to Haversine
        double distanceKm;
        double durationMinutes;

        double[] apiResult = osmClient.getRoute(
                request.getOriginLon(), request.getOriginLat(),
                request.getDestinationLon(), request.getDestinationLat()
        );

        if (apiResult != null) {
            distanceKm = apiResult[0] / 1000.0;
            durationMinutes = apiResult[1] / 60.0;
            factors.add("Route calculated using OSRM API");
        } else {
            distanceKm = haversineCalculator.calculateDistance(
                    request.getOriginLat(), request.getOriginLon(),
                    request.getDestinationLat(), request.getDestinationLon()
            );
            durationMinutes = haversineCalculator.estimateDuration(distanceKm, 50);
            factors.add("Route estimated via straight-line distance (OSRM unavailable)");
        }

        // Step 2: evaluate avoidCheckpoints constraint
        List<RouteResponseDTO.AffectedCheckpoint> affectedCheckpoints = new ArrayList<>();
        if (request.isAvoidCheckpoints()) {
            List<Checkpoint> restrictedCheckpoints = checkpointRepository
                    .findByCurrentStatusIn(List.of(CheckpointStatus.CLOSED, CheckpointStatus.DELAYED));

            for (Checkpoint cp : restrictedCheckpoints) {
                double distFromPath = perpendicularDistanceKm(
                        cp.getLatitude(), cp.getLongitude(),
                        request.getOriginLat(), request.getOriginLon(),
                        request.getDestinationLat(), request.getDestinationLon()
                );

                if (distFromPath <= CORRIDOR_RADIUS_KM) {
                    affectedCheckpoints.add(RouteResponseDTO.AffectedCheckpoint.builder()
                            .id(cp.getId())
                            .name(cp.getName())
                            .status(cp.getCurrentStatus().name())
                            .latitude(cp.getLatitude())
                            .longitude(cp.getLongitude())
                            .build());

                    factors.add(String.format(
                            "Checkpoint '%s' (%s) is on this route — consider an alternate path",
                            cp.getName(), cp.getCurrentStatus().name()));
                    routeModified = true;
                }
            }

            if (affectedCheckpoints.isEmpty()) {
                factors.add("No closed or delayed checkpoints detected along this route");
            }
        }

        // Step 3: evaluate avoidAreas constraint
        List<String> avoidedAreas = new ArrayList<>();
        if (request.getAvoidAreas() != null && !request.getAvoidAreas().isEmpty()) {
            for (String area : request.getAvoidAreas()) {
                try {
                    double[] coords = geocodingService.getCoordinatesFromPlace(area);
                    double distFromPath = perpendicularDistanceKm(
                            coords[0], coords[1],
                            request.getOriginLat(), request.getOriginLon(),
                            request.getDestinationLat(), request.getDestinationLon()
                    );

                    if (distFromPath <= AREA_RADIUS_KM) {
                        avoidedAreas.add(area);
                        factors.add(String.format(
                                "Area '%s' is near this route and was requested to be avoided", area));
                        routeModified = true;
                    } else {
                        factors.add(String.format("Area '%s' does not intersect this route", area));
                    }
                } catch (Exception e) {
                    log.warn("Could not resolve area '{}' to coordinates: {}", area, e.getMessage());
                    factors.add(String.format("Area '%s' could not be resolved — skipped", area));
                }
            }
        }

        factors.add(String.format("Distance: %.1f km, Estimated time: %.0f minutes", distanceKm, durationMinutes));

        RouteResponseDTO.RouteMetadata meta = RouteResponseDTO.RouteMetadata.builder()
                .factors(factors)
                .affectedCheckpoints(affectedCheckpoints.isEmpty() ? null : affectedCheckpoints)
                .avoidedAreas(avoidedAreas.isEmpty() ? null : avoidedAreas)
                .routeModified(routeModified)
                .build();

        double roundedDistance = Math.round(distanceKm * 10.0) / 10.0;

        return RouteResponseDTO.builder()
                .estimatedDistance(roundedDistance)
                .estimatedDuration(durationMinutes)
                .metadata(meta)
                .build();
    }

    /**
     * Calculates the perpendicular distance (in km) from point P to the line segment A→B.
     * Falls back to the minimum of dist(P,A) and dist(P,B) when the projection falls outside
     * the segment.
     */
    private double perpendicularDistanceKm(
            double pLat, double pLon,
            double aLat, double aLon,
            double bLat, double bLon) {

        // Use flat-earth approximation (acceptable for short Palestinian distances)
        double ax = aLon, ay = aLat;
        double bx = bLon, by = bLat;
        double px = pLon, py = pLat;

        double dx = bx - ax;
        double dy = by - ay;

        double lenSq = dx * dx + dy * dy;
        if (lenSq == 0) {
            // A and B are the same point
            return haversineCalculator.calculateDistance(pLat, pLon, aLat, aLon);
        }

        // Parameter t of the projection of P onto line AB (clamped to [0,1])
        double t = Math.max(0, Math.min(1, ((px - ax) * dx + (py - ay) * dy) / lenSq));

        double closestLat = ay + t * dy;
        double closestLon = ax + t * dx;

        return haversineCalculator.calculateDistance(pLat, pLon, closestLat, closestLon);
    }
}