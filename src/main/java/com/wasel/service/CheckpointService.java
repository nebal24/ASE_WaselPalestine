package com.wasel.service;

import com.wasel.dto.CheckpointRequestDTO;
import com.wasel.dto.CheckpointResponseDTO;
import com.wasel.dto.CheckpointStatusHistoryDTO;
import com.wasel.entity.Checkpoint;
import com.wasel.entity.CheckpointStatusHistory;
import com.wasel.entity.User;
import com.wasel.exception.ResourceNotFoundException;
import com.wasel.model.CheckpointStatus;
import com.wasel.repository.CheckpointRepository;
import com.wasel.repository.CheckpointStatusHistoryRepository;
import com.wasel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckpointService {

    private final CheckpointRepository checkpointRepository;
    private final CheckpointStatusHistoryRepository historyRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CheckpointResponseDTO> getAllCheckpoints() {
        return checkpointRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CheckpointResponseDTO getCheckpointById(Long id) {
        return checkpointRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Checkpoint not found with id: " + id));
    }

    @Transactional
    public CheckpointResponseDTO createCheckpoint(CheckpointRequestDTO request, Long createdByUserId) {
        User user = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Checkpoint checkpoint = new Checkpoint();
        checkpoint.setName(request.getName());
        checkpoint.setLatitude(request.getLatitude());
        checkpoint.setLongitude(request.getLongitude());
        checkpoint.setDescription(request.getDescription());
        checkpoint.setCurrentStatus(CheckpointStatus.OPEN);

        Checkpoint saved = checkpointRepository.saveAndFlush(checkpoint);
        saveStatusHistory(saved, CheckpointStatus.OPEN, user);
        return toDTO(saved);
    }

    @Transactional
    public CheckpointResponseDTO updateStatus(Long checkpointId, CheckpointStatus newStatus, Long updatedByUserId) {
        Checkpoint cp = checkpointRepository.findById(checkpointId)
                .orElseThrow(() -> new ResourceNotFoundException("Checkpoint not found"));

        User user = userRepository.findById(updatedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        cp.setCurrentStatus(newStatus);
        Checkpoint saved = checkpointRepository.save(cp);
        saveStatusHistory(saved, newStatus, user);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<CheckpointStatusHistoryDTO> getStatusHistory(Long checkpointId) {
        return historyRepository.findByCheckpointIdOrderByUpdatedAtDesc(checkpointId)
                .stream()
                .map(this::toHistoryDTO)
                .toList();
    }

    private void saveStatusHistory(Checkpoint checkpoint, CheckpointStatus status, User user) {
        CheckpointStatusHistory history = new CheckpointStatusHistory();
        history.setCheckpoint(checkpoint);
        history.setStatus(status);
        history.setUpdatedAt(LocalDateTime.now());
        history.setUpdatedBy(user);
        historyRepository.save(history);
    }

    private CheckpointResponseDTO toDTO(Checkpoint cp) {
        return CheckpointResponseDTO.builder()
                .id(cp.getId())
                .name(cp.getName())
                .latitude(cp.getLatitude())
                .longitude(cp.getLongitude())
                .currentStatus(cp.getCurrentStatus() != null ? cp.getCurrentStatus().name() : null)
                .description(cp.getDescription())
                .build();
    }

    private CheckpointStatusHistoryDTO toHistoryDTO(CheckpointStatusHistory h) {
        return CheckpointStatusHistoryDTO.builder()
                .statusId(h.getStatusId())
                .status(h.getStatus() != null ? h.getStatus().name() : null)
                .updatedAt(h.getUpdatedAt())
                .updatedById(h.getUpdatedBy() != null ? h.getUpdatedBy().getId() : null)
                .updatedByName(h.getUpdatedBy() != null ? h.getUpdatedBy().getName() : null)
                .build();
    }
}