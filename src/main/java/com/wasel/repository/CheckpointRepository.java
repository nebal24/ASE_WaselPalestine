package com.wasel.repository;

import com.wasel.entity.Checkpoint;
import com.wasel.model.CheckpointStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {

    List<Checkpoint> findByCurrentStatusIn(List<CheckpointStatus> statuses);
}
