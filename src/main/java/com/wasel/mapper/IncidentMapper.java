package com.wasel.mapper;

import com.wasel.dto.IncidentDTO;
import com.wasel.dto.UserSummaryDTO;
import com.wasel.dto.CheckpointSummaryDTO;
import com.wasel.entity.Incident;
import com.wasel.entity.User;
import com.wasel.entity.Checkpoint;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public IncidentDTO toDTO(Incident incident) {
        if (incident == null) {
            return null;
        }

        IncidentDTO dto = new IncidentDTO();
        dto.setId(incident.getIncidentId());
        dto.setDescription(incident.getDescription());
        dto.setCategory(incident.getCategory());
        dto.setSeverity(incident.getSeverity());
        dto.setLatitude(incident.getLatitude());
        dto.setLongitude(incident.getLongitude());
        dto.setStatus(incident.getStatus());
        dto.setCreatedAt(incident.getCreatedAt());
        dto.setUpdatedAt(incident.getUpdatedAt());
        dto.setVerifiedAt(incident.getVerifiedAt());

        if (incident.getCreatedBy() != null) {
            UserSummaryDTO createdBy = new UserSummaryDTO();
            createdBy.setId(incident.getCreatedBy().getId());
            createdBy.setName(incident.getCreatedBy().getName());
            createdBy.setEmail(incident.getCreatedBy().getEmail());
            dto.setCreatedBy(createdBy);
        }

        if (incident.getVerifiedBy() != null) {
            UserSummaryDTO verifiedBy = new UserSummaryDTO();
            verifiedBy.setId(incident.getVerifiedBy().getId());
            verifiedBy.setName(incident.getVerifiedBy().getName());
            verifiedBy.setEmail(incident.getVerifiedBy().getEmail());
            dto.setVerifiedBy(verifiedBy);
        }

        if (incident.getCheckpoint() != null) {
            CheckpointSummaryDTO checkpoint = new CheckpointSummaryDTO();
            checkpoint.setId(incident.getCheckpoint().getId());
            checkpoint.setName(incident.getCheckpoint().getName());
            checkpoint.setLatitude(incident.getCheckpoint().getLatitude());
            checkpoint.setLongitude(incident.getCheckpoint().getLongitude());
            checkpoint.setCurrentStatus(incident.getCheckpoint().getCurrentStatus());
            dto.setCheckpoint(checkpoint);
        }

        return dto;
    }

    public Incident toEntity(IncidentRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Incident incident = new Incident();
        incident.setDescription(dto.getDescription());
        incident.setCategory(dto.getCategory());
        incident.setSeverity(dto.getSeverity());
        incident.setLatitude(dto.getLatitude());
        incident.setLongitude(dto.getLongitude());

        return incident;
    }
}