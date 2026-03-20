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
@RestController
@RequestMapping("/api/v1/incidents")
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
            @AuthenticationPrincipal User user) {  // 👈 هيك أحسن
        return new ResponseEntity<>(incidentService.createIncident(incidentDTO, user.getId()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<IncidentDTO> updateIncident(
            @PathVariable Long id,
            @RequestBody IncidentRequestDTO incidentDTO,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(incidentService.updateIncident(id, incidentDTO, user.getId()));
    }

    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<IncidentDTO> verifyIncident(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(incidentService.verifyIncident(id, user.getId()));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<IncidentDTO> closeIncident(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(incidentService.closeIncident(id, user.getId()));
    }

    @DeleteMapping("/{id}")
   // @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteIncident(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        incidentService.deleteIncident(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}