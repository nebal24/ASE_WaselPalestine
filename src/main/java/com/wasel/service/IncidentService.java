package com.wasel.service;

import com.wasel.dto.IncidentDTO;
import com.wasel.dto.IncidentFilterDTO;
import com.wasel.dto.IncidentRequestDTO;
import com.wasel.exception.ResourceNotFoundException;
import com.wasel.exception.UnauthorizedException;
import org.springframework.data.domain.Page;

/**
 * Service interface for Incident operations
 * Defines the contract for incident management business logic
 * All methods declare the operations available for incident handling
 */
public interface IncidentService {

    /**
     * Retrieve all incidents with optional filtering, sorting and pagination
     *
     * @param filterDTO DTO containing filter criteria (category, severity, status, etc.)
     * @return Page of incidents matching the filter criteria
     */
    Page<IncidentDTO> getAllIncidents(IncidentFilterDTO filterDTO);

    /**
     * Retrieve a single incident by its ID
     *
     * @param id The ID of the incident to retrieve
     * @return IncidentDTO containing incident details
     * @throws ResourceNotFoundException if incident with given ID doesn't exist
     */
    IncidentDTO getIncidentById(Long id);

    /**
     * Create a new incident
     *
     * @param incidentDTO DTO containing incident data from request
     * @param userId ID of the user creating the incident
     * @return Created incident with generated ID and timestamps
     * @throws ResourceNotFoundException if user or referenced checkpoint not found
     */
    IncidentDTO createIncident(IncidentRequestDTO incidentDTO, Long userId);

    /**
     * Update an existing incident
     *
     * @param id ID of the incident to update
     * @param incidentDTO Updated incident data
     * @param userId ID of user performing the update
     * @return Updated incident data
     * @throws ResourceNotFoundException if incident not found
     * @throws UnauthorizedException if user lacks permission (not owner, moderator, or admin)
     */
    IncidentDTO updateIncident(Long id, IncidentRequestDTO incidentDTO, Long userId);

    /**
     * Verify an incident (mark as confirmed by moderator)
     * Changes incident status to VERIFIED
     *
     * @param id ID of the incident to verify
     * @param moderatorId ID of the moderator performing verification
     * @return Verified incident with verification timestamp and verifier info
     * @throws ResourceNotFoundException if incident or moderator not found
     */
    IncidentDTO verifyIncident(Long id, Long moderatorId);

    /**
     * Close an incident (mark as resolved)
     * Changes incident status to CLOSED
     *
     * @param id ID of the incident to close
     * @param moderatorId ID of the moderator performing closure
     * @return Closed incident with closure timestamp
     * @throws ResourceNotFoundException if incident not found
     */
    IncidentDTO closeIncident(Long id, Long moderatorId);

    /**
     * Delete an incident permanently
     *
     * @param id ID of the incident to delete
     * @param userId ID of user performing deletion
     * @throws ResourceNotFoundException if incident or user not found
     * @throws UnauthorizedException if user is not ADMIN or MODERATOR
     */
    void deleteIncident(Long id, Long userId);
}