//package com.wasel.mapper;
//
//import com.wasel.dto.IncidentDTO;
//import com.wasel.dto.UserSummaryDTO;
//import com.wasel.dto.CheckpointSummaryDTO;
//import com.wasel.dto.IncidentRequestDTO;
//import com.wasel.entity.Incident;
//import com.wasel.entity.User;
//import com.wasel.entity.Checkpoint;
//import org.springframework.stereotype.Component;
//
///**
// * Mapper component for converting between Incident entities and DTOs
// * Handles the transformation of data between database layer and API layer
// */
//@Component
//public class IncidentMapper {
//
//    /**
//     * Converts an Incident entity to IncidentDTO for API responses
//     * Maps all fields and nested objects (createdBy, verifiedBy, checkpoint)
//     *
//     * @param incident the entity to convert
//     * @return IncidentDTO ready for JSON response, or null if input is null
//     */
//    public IncidentDTO toDTO(Incident incident) {
//        if (incident == null) {
//            return null;
//        }
//
//        IncidentDTO dto = new IncidentDTO();
//
//        // Map primitive fields
//        dto.setId(incident.getIncidentId());
//        dto.setDescription(incident.getDescription());
//        dto.setCategory(incident.getCategory());
//        dto.setSeverity(incident.getSeverity());
//        dto.setLatitude(incident.getLatitude());
//        dto.setLongitude(incident.getLongitude());
//        dto.setStatus(incident.getStatus());
//        dto.setCreatedAt(incident.getCreatedAt());
//        dto.setUpdatedAt(incident.getUpdatedAt());
//        dto.setVerifiedAt(incident.getVerifiedAt());
//
//        // Map creator user (always present as it's required)
//        if (incident.getCreatedBy() != null) {
//            UserSummaryDTO createdBy = new UserSummaryDTO();
//            createdBy.setId(incident.getCreatedBy().getId());
//            createdBy.setName(incident.getCreatedBy().getName());
//            createdBy.setEmail(incident.getCreatedBy().getEmail());
//            dto.setCreatedBy(createdBy);
//        }
//
//        // Map verifier user (optional - may be null)
//        if (incident.getVerifiedBy() != null) {
//            UserSummaryDTO verifiedBy = new UserSummaryDTO();
//            verifiedBy.setId(incident.getVerifiedBy().getId());
//            verifiedBy.setName(incident.getVerifiedBy().getName());
//            verifiedBy.setEmail(incident.getVerifiedBy().getEmail());
//            dto.setVerifiedBy(verifiedBy);
//        }
//
//        // Map associated checkpoint (optional - may be null)
//        if (incident.getCheckpoint() != null) {
//            CheckpointSummaryDTO checkpoint = new CheckpointSummaryDTO();
//            checkpoint.setId(incident.getCheckpoint().getId());
//            checkpoint.setName(incident.getCheckpoint().getName());
//            checkpoint.setLatitude(incident.getCheckpoint().getLatitude());
//            checkpoint.setLongitude(incident.getCheckpoint().getLongitude());
//            // Convert enum to String for JSON response
//            checkpoint.setCurrentStatus(incident.getCheckpoint().getCurrentStatus().toString());
//            dto.setCheckpoint(checkpoint);
//        }
//
//        return dto;
//    }
//
//    /**
//     * Converts an IncidentRequestDTO to Incident entity for database storage
//     * Only maps fields that are provided in the request
//     * System fields (createdAt, status, etc.) are handled by the entity lifecycle
//     *
//     * @param dto the request DTO containing incident data
//     * @return Incident entity ready for persistence, or null if input is null
//     */
//    public Incident toEntity(IncidentRequestDTO dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        Incident incident = new Incident();
//
//        // Map only the fields that come from user request
//        incident.setDescription(dto.getDescription());
//        incident.setCategory(dto.getCategory());
//        incident.setSeverity(dto.getSeverity());
//        incident.setLatitude(dto.getLatitude());
//        incident.setLongitude(dto.getLongitude());
//
//        // Note: The following fields are NOT mapped here:
//        // - id (auto-generated)
//        // - status (defaults to OPEN in @PrePersist)
//        // - createdAt (set in @PrePersist)
//        // - createdBy (set in service layer)
//        // - verifiedBy (set in service when verifying)
//        // - checkpoint (set in service if provided)
//
//        return incident;
//    }
//}