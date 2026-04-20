package com.wasel.repository;

import com.wasel.entity.Checkpoint;
import com.wasel.model.CheckpointStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {

    List<Checkpoint> findByCurrentStatusIn(List<CheckpointStatus> statuses);

    /**
     * Native query — Query 1: Top Violated Checkpoints
     *
     * For each checkpoint, count how many incidents are linked to it and compute
     * a numeric average severity (LOW=1, MEDIUM=2, HIGH=3).
     * Results are ordered by incident frequency descending so the most
     * problematic checkpoints appear first.
     *
     * Used by: Admin dashboard to identify hotspot checkpoints.
     */
    @Query(value = """
            SELECT
                c.id                        AS checkpoint_id,
                c.name                      AS checkpoint_name,
                COUNT(i.incident_id)        AS incident_count,
                -- Map enum strings to numeric scores, then average them
                AVG(CASE
                    WHEN i.severity = 'LOW'    THEN 1.0
                    WHEN i.severity = 'MEDIUM' THEN 2.0
                    WHEN i.severity = 'HIGH'   THEN 3.0
                    ELSE NULL
                END)                        AS average_severity
            FROM checkpoints c
            LEFT JOIN incidents i ON i.checkpoint_id = c.id
            GROUP BY c.id, c.name
            ORDER BY COUNT(i.incident_id) DESC,
                     AVG(CASE
                         WHEN i.severity = 'LOW'    THEN 1.0
                         WHEN i.severity = 'MEDIUM' THEN 2.0
                         WHEN i.severity = 'HIGH'   THEN 3.0
                         ELSE NULL
                     END) DESC NULLS LAST
            """,
            nativeQuery = true)
    List<Object[]> findTopViolatedCheckpoints();
}
