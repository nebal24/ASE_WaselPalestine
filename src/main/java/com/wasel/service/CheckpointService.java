package com.wasel.service;

import com.wasel.entity.Checkpoint;
import com.wasel.entity.CheckpointStatusHistory;
import com.wasel.entity.Incident;
import com.wasel.entity.User;
import com.wasel.model.CheckpointStatus;
import com.wasel.model.IncidentStatus;
import com.wasel.repository.CheckpointRepository;
import com.wasel.repository.CheckpointStatusHistoryRepository;
import com.wasel.repository.IncidentRepository;
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
    private final IncidentRepository incidentRepository;

    @Transactional
    public Checkpoint createCheckpoint(Checkpoint checkpoint, Long createdByUserId) {
        User user = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        checkpoint.setCurrentStatus(CheckpointStatus.OPEN);
        Checkpoint saved = checkpointRepository.saveAndFlush(checkpoint);
        saveStatusHistory(saved, CheckpointStatus.OPEN, user);
        return saved;
    }

    @Transactional
    public Checkpoint updateStatus(Long checkpointId, CheckpointStatus newStatus, Long updatedByUserId) {
        Checkpoint cp = checkpointRepository.findById(checkpointId)
                .orElseThrow(() -> new RuntimeException("Checkpoint not found"));

        User user = userRepository.findById(updatedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        cp.setCurrentStatus(newStatus);
        Checkpoint saved = checkpointRepository.save(cp);

        saveStatusHistory(saved, newStatus, user);
        return saved;
    }

    private void saveStatusHistory(Checkpoint checkpoint, CheckpointStatus status, User user) {
        CheckpointStatusHistory history = new CheckpointStatusHistory();
        history.setCheckpoint(checkpoint);
        history.setStatus(status);
        history.setUpdatedAt(LocalDateTime.now());
        history.setUpdatedBy(user);
        historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<CheckpointStatusHistory> getStatusHistory(Long checkpointId) {
        return historyRepository.findByCheckpointIdOrderByUpdatedAtDesc(checkpointId);
    }

    @Transactional
    public Incident createIncidentForCheckpoint(Long checkpointId, Incident incident, Long createdByUserId) {
        Checkpoint cp = checkpointRepository.findById(checkpointId)
                .orElseThrow(() -> new RuntimeException("Checkpoint not found"));

        User user = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        incident.setCheckpoint(cp);
        incident.setCreatedBy(user);
        incident.setCreatedAt(LocalDateTime.now());
        incident.setStatus(IncidentStatus.OPEN);

        return incidentRepository.save(incident);
    }
}