package com.wasel.repository;

import com.wasel.entity.ModerationAction;
import com.wasel.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModerationActionRepository extends JpaRepository<ModerationAction, Long> {

    List<ModerationAction> findByReportOrderByCreatedAtDesc(Report report);
}