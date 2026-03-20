package com.wasel.controller;

import com.wasel.dto.IncidentDTO;
import com.wasel.dto.IncidentFilterDTO;
import com.wasel.dto.IncidentRequestDTO;
import com.wasel.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.wasel.entity.User;

/**
 * REST Controller for managing incident operations
 * Handles all HTTP requests related to incidents
 * Base path: /api/v1/incidents
 */
@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
public class IncidentController {

    // Service layer for incident business logic
    private final IncidentService incidentService;

    /**
     * Get all incidents with filtering, sorting and pagination
     * Endpoint: GET /api/v1/incidents
     *
     * @param filterDTO contains filtering criteria (category, severity, status, etc.)
     * @return Page of incidents matching the filter criteria
     */
    @GetMapping
    public ResponseEntity<Page<IncidentDTO>> getAllIncidents(IncidentFilterDTO filterDTO) {
        return ResponseEntity.ok(incidentService.getAllIncidents(filterDTO));
    }

    /**
     * Get a specific incident by its ID
     * Endpoint: GET /api/v1/incidents/{id}
     *
     * @param id Incident ID to retrieve
     * @return Incident details if found
     * @throws ResourceNotFoundException if incident not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<IncidentDTO> getIncidentById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }

    /**
     * Create a new incident
     * Endpoint: POST /api/v1/incidents
     * Access: USER, MODERATOR, ADMIN
     *
     * @param incidentDTO Incident data from request body
     * @param user Currently authenticated user (from JWT token)
     * @return Created incident with status 201
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<IncidentDTO> createIncident(
            @RequestBody IncidentRequestDTO incidentDTO,
            @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(
                incidentService.createIncident(incidentDTO, user.getId()),
                HttpStatus.CREATED
        );
    }

    /**
     * Update an existing incident
     * Endpoint: PUT /api/v1/incidents/{id}
     * Access: MODERATOR, ADMIN only
     *
     * @param id Incident ID to update
     * @param incidentDTO Updated incident data
     * @param user Currently authenticated user
     * @return Updated incident details
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<IncidentDTO> updateIncident(
            @PathVariable Long id,
            @RequestBody IncidentRequestDTO incidentDTO,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(incidentService.updateIncident(id, incidentDTO, user.getId()));
    }

    /**
     * Verify an incident (mark as verified by moderator)
     * Endpoint: PATCH /api/v1/incidents/{id}/verify
     * Access: MODERATOR, ADMIN only
     *
     * @param id Incident ID to verify
     * @param user Currently authenticated user (moderator/admin)
     * @return Verified incident with verification details
     */
    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<IncidentDTO> verifyIncident(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(incidentService.verifyIncident(id, user.getId()));
    }

    /**
     * Close an incident (mark as resolved)
     * Endpoint: PATCH /api/v1/incidents/{id}/close
     * Access: MODERATOR, ADMIN only
     *
     * @param id Incident ID to close
     * @param user Currently authenticated user
     * @return Closed incident details
     */
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<IncidentDTO> closeIncident(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(incidentService.closeIncident(id, user.getId()));
    }

    /**
     * Delete an incident
     * Endpoint: DELETE /api/v1/incidents/{id}
     * Access: MODERATOR, ADMIN (temporarily commented for testing)
     *
     * @param id Incident ID to delete
     * @param user Currently authenticated user
     * @return No content (204) on successful deletion
     */
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteIncident(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        incidentService.deleteIncident(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}