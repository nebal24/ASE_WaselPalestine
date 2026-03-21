//package com.wasel.service.impl;
//
//import com.wasel.dto.IncidentDTO;
//import com.wasel.dto.IncidentFilterDTO;
//import com.wasel.dto.IncidentRequestDTO;
//import com.wasel.entity.Incident;
//import com.wasel.entity.User;
//import com.wasel.entity.Checkpoint;
//import com.wasel.model.IncidentStatus;
//import com.wasel.repository.IncidentRepository;
//import com.wasel.repository.UserRepository;
//import com.wasel.repository.CheckpointRepository;
//import com.wasel.service.IncidentService;
//import com.wasel.exception.ResourceNotFoundException;
//import com.wasel.exception.UnauthorizedException;
//import com.wasel.mapper.IncidentMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * Implementation of IncidentService interface
// * Handles all business logic for incident operations
// */
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class IncidentServiceImpl implements IncidentService {
//
//    // Repository dependencies
//    private final IncidentRepository incidentRepository;
//    private final UserRepository userRepository;
//    private final CheckpointRepository checkpointRepository;
//
//    // Mapper for entity-DTO conversion
//    private final IncidentMapper incidentMapper;
//
//    /**
//     * Get all incidents with filtering, sorting and pagination
//     *
//     * @param filterDTO contains filter criteria (category, severity, status, etc.)
//     * @return Page of incidents matching the filters
//     */
//    @Override
//    public Page<IncidentDTO> getAllIncidents(IncidentFilterDTO filterDTO) {
//        // Create pageable object with sorting
//        Pageable pageable = createPageable(filterDTO);
//
//        // Fetch incidents with filters
//        Page<Incident> incidents = incidentRepository.findWithFilters(
//                filterDTO.getCategory(),
//                filterDTO.getSeverity(),
//                filterDTO.getStatus(),
//                filterDTO.getCheckpointId(),
//                filterDTO.getLatitude(),
//                filterDTO.getLongitude(),
//                filterDTO.getRadius(),
//                pageable
//        );
//
//        // Convert entities to DTOs and return
//        return incidents.map(incidentMapper::toDTO);
//    }
//
//    /**
//     * Get a single incident by ID
//     *
//     * @param id incident ID
//     * @return incident DTO
//     * @throws ResourceNotFoundException if incident not found
//     */
//    @Override
//    public IncidentDTO getIncidentById(Long id) {
//        Incident incident = incidentRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));
//        return incidentMapper.toDTO(incident);
//    }
//
//    /**
//     * Create a new incident
//     *
//     * @param incidentDTO incident data from request
//     * @param userId ID of user creating the incident
//     * @return created incident DTO
//     * @throws ResourceNotFoundException if user or checkpoint not found
//     */
//    @Override
//    public IncidentDTO createIncident(IncidentRequestDTO incidentDTO, Long userId) {
//        // Find the creator user
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        // Convert DTO to entity
//        Incident incident = incidentMapper.toEntity(incidentDTO);
//        incident.setCreatedBy(user);
//
//        // If checkpoint ID provided, find and set checkpoint
//        if (incidentDTO.getCheckpointId() != null) {
//            Checkpoint checkpoint = checkpointRepository.findById(incidentDTO.getCheckpointId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Checkpoint not found"));
//            incident.setCheckpoint(checkpoint);
//        }
//
//        // Save to database and return DTO
//        Incident savedIncident = incidentRepository.save(incident);
//        return incidentMapper.toDTO(savedIncident);
//    }
//
//    /**
//     * Update an existing incident
//     *
//     * @param id incident ID to update
//     * @param incidentDTO updated incident data
//     * @param userId ID of user performing update
//     * @return updated incident DTO
//     * @throws ResourceNotFoundException if incident/user/checkpoint not found
//     * @throws UnauthorizedException if user lacks permission
//     */
//    @Override
//    public IncidentDTO updateIncident(Long id, IncidentRequestDTO incidentDTO, Long userId) {
//        // Find incident to update
//        Incident incident = incidentRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));
//
//        // Check if user is creator or has moderator/admin role
//        if (!incident.getCreatedBy().getId().equals(userId)) {
//            User user = userRepository.findById(userId).get();
//            if (user.getRole().toString().equals("USER")) {
//                throw new UnauthorizedException("You can only update your own incidents");
//            }
//        }
//
//        // Update fields from DTO
//        incident.setDescription(incidentDTO.getDescription());
//        incident.setCategory(incidentDTO.getCategory());
//        incident.setSeverity(incidentDTO.getSeverity());
//        incident.setLatitude(incidentDTO.getLatitude());
//        incident.setLongitude(incidentDTO.getLongitude());
//
//        // Update checkpoint if provided
//        if (incidentDTO.getCheckpointId() != null) {
//            Checkpoint checkpoint = checkpointRepository.findById(incidentDTO.getCheckpointId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Checkpoint not found"));
//            incident.setCheckpoint(checkpoint);
//        }
//
//        // Save and return
//        Incident updatedIncident = incidentRepository.save(incident);
//        return incidentMapper.toDTO(updatedIncident);
//    }
//
//    /**
//     * Verify an incident (mark as confirmed)
//     *
//     * @param id incident ID to verify
//     * @param moderatorId ID of moderator performing verification
//     * @return verified incident DTO
//     * @throws ResourceNotFoundException if incident or moderator not found
//     */
//    @Override
//    public IncidentDTO verifyIncident(Long id, Long moderatorId) {
//        // Find incident
//        Incident incident = incidentRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));
//
//        // Find moderator
//        User moderator = userRepository.findById(moderatorId)
//                .orElseThrow(() -> new ResourceNotFoundException("Moderator not found"));
//
//        // Update status and set verifier
//        incident.setStatus(IncidentStatus.VERIFIED);
//        incident.setVerifiedBy(moderator);
//
//        // Save and return
//        Incident verifiedIncident = incidentRepository.save(incident);
//        return incidentMapper.toDTO(verifiedIncident);
//    }
//
//    /**
//     * Close an incident (mark as resolved)
//     *
//     * @param id incident ID to close
//     * @param moderatorId ID of moderator performing closure
//     * @return closed incident DTO
//     * @throws ResourceNotFoundException if incident not found
//     */
//    @Override
//    public IncidentDTO closeIncident(Long id, Long moderatorId) {
//        // Find incident
//        Incident incident = incidentRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));
//
//        // Update status to CLOSED
//        incident.setStatus(IncidentStatus.CLOSED);
//
//        // Save and return
//        Incident closedIncident = incidentRepository.save(incident);
//        return incidentMapper.toDTO(closedIncident);
//    }
//
//    /**
//     * Delete an incident
//     *
//     * @param id incident ID to delete
//     * @param userId ID of user performing deletion
//     * @throws ResourceNotFoundException if incident or user not found
//     * @throws UnauthorizedException if user is not ADMIN or MODERATOR
//     */
//    @Override
//    public void deleteIncident(Long id, Long userId) {
//        // Find incident to delete
//        Incident incident = incidentRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));
//
//        // Find user attempting deletion
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        // Check if user has delete permission (ADMIN or MODERATOR)
//        String role = user.getRole().toString();
//        if (!role.equals("ADMIN") && !role.equals("MODERATOR")) {
//            throw new UnauthorizedException("Only admins or moderators can delete incidents");
//        }
//
//        // Delete the incident
//        incidentRepository.delete(incident);
//    }
//
//    /**
//     * Helper method to create Pageable object from filter DTO
//     * Handles sorting configuration
//     *
//     * @param filterDTO contains pagination and sorting parameters
//     * @return configured Pageable object
//     */
//    private Pageable createPageable(IncidentFilterDTO filterDTO) {
//        // Default sort by createdAt descending (newest first)
//        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
//
//        // Apply custom sorting if specified
//        if (filterDTO.getSortBy() != null) {
//            Sort.Direction direction = filterDTO.getSortDirection() != null &&
//                    filterDTO.getSortDirection().equalsIgnoreCase("ASC") ?
//                    Sort.Direction.ASC : Sort.Direction.DESC;
//            sort = Sort.by(direction, filterDTO.getSortBy());
//        }
//
//        // Create pageable with page number, page size, and sort
//        return PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), sort);
//    }
//}