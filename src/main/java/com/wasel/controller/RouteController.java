package com.wasel.controller;

import com.wasel.dto.RouteRequestDTO;
import com.wasel.dto.RouteResponseDTO;
import com.wasel.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for route estimation endpoints
 * Endpoint: /api/v1/routes
 */
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    /**
     * Calculate route between two geographic points
     *
     * Example:
     * GET /api/v1/routes?startLat=32.2&startLng=35.3&endLat=31.5&endLng=34.5
     *
     * @param startLat Starting point latitude
     * @param startLng Starting point longitude
     * @param endLat Destination latitude
     * @param endLng Destination longitude
     * @param avoidCheckpoints Whether to avoid checkpoints (optional)
     * @return Route information with distance, duration, and metadata
     */
    @GetMapping
    public ResponseEntity<RouteResponseDTO> estimateRoute(
            @RequestParam Double startLat,
            @RequestParam Double startLng,
            @RequestParam Double endLat,
            @RequestParam Double endLng,
            @RequestParam(defaultValue = "false") boolean avoidCheckpoints) {

        RouteRequestDTO request = new RouteRequestDTO();
        request.setStartLat(startLat);
        request.setStartLng(startLng);
        request.setEndLat(endLat);
        request.setEndLng(endLng);
        request.setAvoidCheckpoints(avoidCheckpoints);

        RouteResponseDTO response = routeService.estimateRoute(request);

        return ResponseEntity.ok(response);
    }
}