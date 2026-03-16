package com.wasel.service;

import com.wasel.entity.CheckPoint;
import com.wasel.entity.CheckpointStatusHistory;
import com.wasel.entity.User;
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

    // النقطة 1: إنشاء Checkpoint + أول سجل في التاريخ
    @Transactional
    public CheckPoint createCheckpoint(CheckPoint checkpoint, Long createdByUserId) {
        User user = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        checkpoint.setCurrentStatus(CheckpointStatus.OPEN);
        CheckPoint saved = checkpointRepository.save(checkpoint);

        saveStatusHistory(saved, CheckpointStatus.OPEN, user);
        return saved;
    }

    // النقطة 2: تحديث الحالة + حفظ في التاريخ
    @Transactional
    public CheckPoint updateStatus(Long checkpointId, CheckpointStatus newStatus, Long updatedByUserId) {
        CheckPoint cp = checkpointRepository.findById(checkpointId)
                .orElseThrow(() -> new RuntimeException("Checkpoint not found"));

        User user = userRepository.findById(updatedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        cp.setCurrentStatus(newStatus);
        CheckPoint saved = checkpointRepository.save(cp);

        saveStatusHistory(saved, newStatus, user);
        return saved;
    }

    private void saveStatusHistory(CheckPoint checkpoint, CheckpointStatus status, User user) {
        CheckpointStatusHistory history = new CheckpointStatusHistory();
        history.setCheckpoint(checkpoint);
        history.setStatus(status);
        history.setUpdatedAt(LocalDateTime.now());
        history.setUpdatedBy(user);
        historyRepository.save(history);
    }

    // جلب تاريخ الحالات (للاختبار فقط، للتأكيد على النقطة 2)
    public List<CheckpointStatusHistory> getStatusHistory(Long checkpointId) {
        CheckPoint cp = checkpointRepository.findById(checkpointId)
                .orElseThrow(() -> new RuntimeException("Checkpoint not found with id: " + checkpointId));
        return cp.getStatusHistory();
    }
    // لإنشاء Incident مرتبط بحاجز (للنقطة 1: road closures, delays, hazardous conditions)
    @Transactional
    public Incident createIncidentForCheckpoint(Long checkpointId, Incident incident, Long createdByUserId) {
        CheckPoint cp = checkpointRepository.findById(checkpointId)
                .orElseThrow(() -> new RuntimeException("Checkpoint not found"));

        User user = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        incident.setCheckpoint(cp);
        incident.setCreatedBy(user);
        incident.setCreatedAt(LocalDateTime.now());
        incident.setStatus(IncidentStatus.OPEN);  // افتراضي

        return incidentRepository.save(incident);
    }
}