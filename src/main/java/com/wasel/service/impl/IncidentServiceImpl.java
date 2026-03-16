package com.wasel.service.impl;

import com.wasel.dto.IncidentDTO;
import com.wasel.dto.IncidentFilterDTO;
import com.wasel.dto.IncidentRequestDTO;
import com.wasel.entity.Incident;
import com.wasel.entity.User;
import com.wasel.entity.CheckPoint;
import com.wasel.model.IncidentStatus;
import com.wasel.repository.IncidentRepository;
import com.wasel.repository.UserRepository;
import com.wasel.repository.CheckpointRepository;
import com.wasel.service.IncidentService;
import com.wasel.exception.ResourceNotFoundException;
import com.wasel.exception.UnauthorizedException;
import com.wasel.mapper.IncidentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final CheckpointRepository checkpointRepository;
    private final IncidentMapper incidentMapper;

    @Override
    public Page<IncidentDTO> getAllIncidents(IncidentFilterDTO filterDTO) {
        Pageable pageable = createPageable(filterDTO);

        Page<Incident> incidents = incidentRepository.findWithFilters(
                filterDTO.getCategory(),
                filterDTO.getSeverity(),
                filterDTO.getStatus(),
                filterDTO.getCheckpointId(),
                filterDTO.getLatitude(),
                filterDTO.getLongitude(),
                filterDTO.getRadius(),
                pageable
        );

        return incidents.map(incidentMapper::toDTO);
    }

    @Override
    public IncidentDTO getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));
        return incidentMapper.toDTO(incident);
    }

    @Override
    public IncidentDTO createIncident(IncidentRequestDTO incidentDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Incident incident = incidentMapper.toEntity(incidentDTO);
        incident.setCreatedBy(user);

        if (incidentDTO.getCheckpointId() != null) {
            CheckPoint checkpoint = checkpointRepository.findById(incidentDTO.getCheckpointId())
                    .orElseThrow(() -> new ResourceNotFoundException("Checkpoint not found"));
            incident.setCheckpoint(checkpoint);
        }

        Incident savedIncident = incidentRepository.save(incident);
        return incidentMapper.toDTO(savedIncident);
    }

    @Override
    public IncidentDTO updateIncident(Long id, IncidentRequestDTO incidentDTO, Long userId) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

        // تأكد إن المستخدم هو منشئ الحادث أو مشرف
        if (!incident.getCreatedBy().getId().equals(userId)) {
            User user = userRepository.findById(userId).get();
            if (user.getRole().toString().equals("USER")) {
                throw new UnauthorizedException("You can only update your own incidents");
            }
        }

        // Update fields
        incident.setDescription(incidentDTO.getDescription());
        incident.setCategory(incidentDTO.getCategory());
        incident.setSeverity(incidentDTO.getSeverity());
        incident.setLatitude(incidentDTO.getLatitude());
        incident.setLongitude(incidentDTO.getLongitude());

        if (incidentDTO.getCheckpointId() != null) {
            CheckPoint checkpoint = checkpointRepository.findById(incidentDTO.getCheckpointId())
                    .orElseThrow(() -> new ResourceNotFoundException("Checkpoint not found"));
            incident.setCheckpoint(checkpoint);
        }

        Incident updatedIncident = incidentRepository.save(incident);
        return incidentMapper.toDTO(updatedIncident);
    }

    @Override
    public IncidentDTO verifyIncident(Long id, Long moderatorId) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

        User moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Moderator not found"));

        incident.setStatus(IncidentStatus.VERIFIED);
        incident.setVerifiedBy(moderator);

        Incident verifiedIncident = incidentRepository.save(incident);
        return incidentMapper.toDTO(verifiedIncident);
    }

    @Override
    public IncidentDTO closeIncident(Long id, Long moderatorId) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

        incident.setStatus(IncidentStatus.CLOSED);

        Incident closedIncident = incidentRepository.save(incident);
        return incidentMapper.toDTO(closedIncident);
    }

    @Override
    public void deleteIncident(Long id, Long userId) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

        // فقط الـ Admin يقدر يحذف
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getRole().toString().equals("ADMIN")) {
            throw new UnauthorizedException("Only admins can delete incidents");
        }

        incidentRepository.delete(incident);
    }

    private Pageable createPageable(IncidentFilterDTO filterDTO) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        if (filterDTO.getSortBy() != null) {
            Sort.Direction direction = filterDTO.getSortDirection() != null &&
                    filterDTO.getSortDirection().equalsIgnoreCase("ASC") ?
                    Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(direction, filterDTO.getSortBy());
        }

        return PageRequest.of(filterDTO.getPage(), filterDTO.getSize(), sort);
    }
}