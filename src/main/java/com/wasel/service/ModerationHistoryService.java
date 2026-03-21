package com.wasel.service;

import com.wasel.dto.ModerationActionResponse;
import com.wasel.entity.ModerationAction;
import com.wasel.entity.Report;
import com.wasel.exception.ResourceNotFoundException;
import com.wasel.repository.ModerationActionRepository;
import com.wasel.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModerationHistoryService {

    private final ModerationActionRepository moderationActionRepository;
    private final ReportRepository reportRepository;

    public ModerationHistoryService(ModerationActionRepository moderationActionRepository,
                                    ReportRepository reportRepository) {
        this.moderationActionRepository = moderationActionRepository;
        this.reportRepository = reportRepository;
    }

    public List<ModerationActionResponse> getReportHistory(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));

        List<ModerationAction> actions = moderationActionRepository.findByReportOrderByCreatedAtDesc(report);

        return actions.stream()
                .map(action -> new ModerationActionResponse(
                        action.getId(),
                        action.getReport().getReportId(),
                        action.getPerformedBy() != null ? action.getPerformedBy().getId() : null,
                        action.getActionType(),
                        action.getReason(),
                        action.getCreatedAt()
                ))
                .toList();
    }
}