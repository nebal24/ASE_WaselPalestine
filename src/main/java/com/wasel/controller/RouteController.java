package com.wasel.controller;

import com.wasel.dto.RouteRequestDTO;
import com.wasel.dto.RouteResponseDTO;
import com.wasel.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    public ResponseEntity<RouteResponseDTO> estimateRoute(
            @RequestParam Double originLat,
            @RequestParam Double originLon,
            @RequestParam Double destinationLat,
            @RequestParam Double destinationLon,
            @RequestParam(defaultValue = "false") boolean avoidCheckpoints) {

        RouteRequestDTO request = new RouteRequestDTO();
        request.setOriginLat(originLat);
        request.setOriginLon(originLon);
        request.setDestinationLat(destinationLat);
        request.setDestinationLon(destinationLon);
        request.setAvoidCheckpoints(avoidCheckpoints);

        RouteResponseDTO response = routeService.estimateRoute(request);
        return ResponseEntity.ok(response);
    }
}