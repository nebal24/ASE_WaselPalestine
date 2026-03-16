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

@RestController
@RequestMapping("/api/v1/incidents")  // versioned API
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping
    public ResponseEntity<Page<IncidentDTO>> getAllIncidents(IncidentFilterDTO filterDTO) {
        return ResponseEntity.ok(incidentService.getAllIncidents(filterDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentDTO> getIncidentById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<IncidentDTO> createIncident(
            @RequestBody IncidentRequestDTO incidentDTO,
            @RequestAttribute Long userId) {  // هنستخدم JWT عشان نجيب userId
        return new ResponseEntity<>(incidentService.createIncident(incidentDTO, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<IncidentDTO> updateIncident(
            @PathVariable Long id,
            @RequestBody IncidentRequestDTO incidentDTO,
            @RequestAttribute Long userId) {
        return ResponseEntity.ok(incidentService.updateIncident(id, incidentDTO, userId));
    }

    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<IncidentDTO> verifyIncident(
            @PathVariable Long id,
            @RequestAttribute Long moderatorId) {
        return ResponseEntity.ok(incidentService.verifyIncident(id, moderatorId));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<IncidentDTO> closeIncident(
            @PathVariable Long id,
            @RequestAttribute Long moderatorId) {
        return ResponseEntity.ok(incidentService.closeIncident(id, moderatorId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteIncident(
            @PathVariable Long id,
            @RequestAttribute Long userId) {
        incidentService.deleteIncident(id, userId);
        return ResponseEntity.noContent().build();
    }
}