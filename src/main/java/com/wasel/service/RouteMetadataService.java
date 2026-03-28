package com.wasel.service;

import com.wasel.dto.RouteRequestDTO;
import com.wasel.dto.RouteResponseDTO;
import com.wasel.dto.RouteResponseDTO.AffectedCheckpoint;
import com.wasel.dto.RouteResponseDTO.RouteMetadata;
import com.wasel.entity.Checkpoint;
import com.wasel.model.CheckpointStatus;
import com.wasel.model.IncidentStatus;
import com.wasel.repository.CheckpointRepository;
import com.wasel.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for:
 * 1. Building explanatory metadata for route responses
 * 2. Detecting affected checkpoints along the route
 * 3. Applying avoidCheckpoints and avoidAreas constraints
 *
 * This service is Person 2's responsibility in Feature 3.
 * It works on top of the base route provided by Person 1 (RouteService).
 */
@Service
@RequiredArgsConstructor
public class RouteMetadataService {

    private final CheckpointRepository checkpointRepository;
    private final IncidentRepository incidentRepository;

    // Radius in km — checkpoints within this distance from the route are considered "on the route"
    private static final double ROUTE_PROXIMITY_KM = 2.0;

    /**
     * Build full metadata for a route response.
     * Called after Person 1's RouteService provides distance and duration.
     *
     * @param request the original route request with constraints
     * @param baseDistance distance returned by Person 1 (in km)
     * @param baseDuration duration returned by Person 1 (in minutes)
     * @return complete RouteResponseDTO with metadata attached
     */
    public RouteResponseDTO buildRouteWithMetadata(RouteRequestDTO request,
                                                   double baseDistance,
                                                   double baseDuration) {
        List<String> factors = new ArrayList<>();
        List<AffectedCheckpoint> affectedCheckpoints = new ArrayList<>();
        List<String> avoidedAreas = new ArrayList<>();
        boolean routeModified = false;

        // Step 1: Find all checkpoints near the route
        List<Checkpoint> allCheckpoints = checkpointRepository.findAll();
        List<Checkpoint> nearbyCheckpoints = findCheckpointsNearRoute(
                allCheckpoints,
                request.getOriginLat(), request.getOriginLon(),
                request.getDestinationLat(), request.getDestinationLon()
        );

        // Step 2: Check each nearby checkpoint for issues
        for (Checkpoint cp : nearbyCheckpoints) {
            if (cp.getCurrentStatus() == CheckpointStatus.CLOSED) {
                affectedCheckpoints.add(buildAffectedCheckpoint(cp));
                factors.add("Checkpoint '" + cp.getName() + "' is CLOSED on this route");

                // If user wants to avoid checkpoints, note that route was modified
                if (request.isAvoidCheckpoints()) {
                    factors.add("Route adjusted to avoid closed checkpoint: " + cp.getName());
                    routeModified = true;
                }
            } else if (cp.getCurrentStatus() == CheckpointStatus.DELAYED) {
                affectedCheckpoints.add(buildAffectedCheckpoint(cp));
                factors.add("Checkpoint '" + cp.getName() + "' has DELAYS — expect longer wait times");
            }
        }

        // Step 3: Check for active incidents near the route
        long activeIncidents = incidentRepository.findAll().stream()
                .filter(i -> i.getStatus() == IncidentStatus.OPEN || i.getStatus() == IncidentStatus.VERIFIED)
                .filter(i -> isNearRoute(
                        i.getLatitude(), i.getLongitude(),
                        request.getOriginLat(), request.getOriginLon(),
                        request.getDestinationLat(), request.getDestinationLon()
                ))
                .count();

        if (activeIncidents > 0) {
            factors.add("There are " + activeIncidents + " active incident(s) near this route");
        }

        // Step 4: Apply avoidAreas constraint
        if (request.getAvoidAreas() != null && !request.getAvoidAreas().isEmpty()) {
            for (String area : request.getAvoidAreas()) {
                avoidedAreas.add(area);
                factors.add("Route adjusted to avoid area: " + area);
                routeModified = true;
            }
        }

        // Step 5: If no issues found, add a clear factor
        if (factors.isEmpty()) {
            factors.add("No known obstacles on this route");
        }

        // Step 6: Build and return the full response
        RouteMetadata metadata = RouteMetadata.builder()
                .factors(factors)
                .affectedCheckpoints(affectedCheckpoints)
                .avoidedAreas(avoidedAreas)
                .routeModified(routeModified)
                .build();

        return RouteResponseDTO.builder()
                .estimatedDistance(baseDistance)
                .estimatedDuration(baseDuration)
                .metadata(metadata)
                .build();
    }

    /**
     * Filter checkpoints that are near the straight line between origin and destination.
     * Uses a simple midpoint proximity check as a heuristic.
     */
    private List<Checkpoint> findCheckpointsNearRoute(List<Checkpoint> checkpoints,
                                                      double originLat, double originLon,
                                                      double destLat, double destLon) {
        List<Checkpoint> result = new ArrayList<>();
        for (Checkpoint cp : checkpoints) {
            if (isNearRoute(cp.getLatitude(), cp.getLongitude(),
                    originLat, originLon, destLat, destLon)) {
                result.add(cp);
            }
        }
        return result;
    }

    /**
     * Check if a point (pointLat, pointLon) is near the route from origin to destination.
     * Uses distance from point to the midpoint of the route as a simple heuristic.
     *
     * @return true if within ROUTE_PROXIMITY_KM of the route midpoint
     */
    private boolean isNearRoute(double pointLat, double pointLon,
                                double originLat, double originLon,
                                double destLat, double destLon) {
        // Use midpoint of route as approximation
        double midLat = (originLat + destLat) / 2;
        double midLon = (originLon + destLon) / 2;

        double distToMid = haversineDistance(pointLat, pointLon, midLat, midLon);
        double routeLength = haversineDistance(originLat, originLon, destLat, destLon);

        // Point is "near route" if within ROUTE_PROXIMITY_KM or within half the route length
        return distToMid <= Math.max(ROUTE_PROXIMITY_KM, routeLength / 2);
    }

    /**
     * Calculate distance between two coordinates using Haversine formula.
     *
     * @return distance in kilometers
     */
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

    /**
     * Convert Checkpoint entity to AffectedCheckpoint DTO
     */
    private AffectedCheckpoint buildAffectedCheckpoint(Checkpoint cp) {
        return AffectedCheckpoint.builder()
                .id(cp.getId())
                .name(cp.getName())
                .status(cp.getCurrentStatus().name())
                .latitude(cp.getLatitude())
                .longitude(cp.getLongitude())
                .build();
    }
}