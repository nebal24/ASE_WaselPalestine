package com.wasel.repository;

import com.wasel.entity.CheckpointStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckpointStatusHistoryRepository extends JpaRepository<CheckpointStatusHistory, Long> {
    List<CheckpointStatusHistory> findByCheckpointId(Long checkpointId);

}