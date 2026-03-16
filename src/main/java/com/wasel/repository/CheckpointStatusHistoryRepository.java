package com.wasel.repository;

import com.wasel.entity.CheckpointStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckpointStatusHistoryRepository extends JpaRepository<CheckpointStatusHistory, Long> {
}