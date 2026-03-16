package com.wasel.service;



import com.wasel.dto.IncidentDTO;
import com.wasel.dto.IncidentFilterDTO;
import com.wasel.dto.IncidentRequestDTO;
import org.springframework.data.domain.Page;

public interface IncidentService {
    Page<IncidentDTO> getAllIncidents(IncidentFilterDTO filterDTO);
    IncidentDTO getIncidentById(Long id);
    IncidentDTO createIncident(IncidentRequestDTO incidentDTO, Long userId);
    IncidentDTO updateIncident(Long id, IncidentRequestDTO incidentDTO, Long userId);
    IncidentDTO verifyIncident(Long id, Long moderatorId);
    IncidentDTO closeIncident(Long id, Long moderatorId);
    void deleteIncident(Long id, Long userId);
}