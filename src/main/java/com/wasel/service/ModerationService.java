package com.wasel.service;

import com.wasel.entity.Report;
import com.wasel.entity.User;
import com.wasel.exception.ResourceNotFoundException;
import com.wasel.exception.UnauthorizedException;
import com.wasel.model.ModerationActionType;
import com.wasel.model.ReportStatus;
import com.wasel.model.Role;
import com.wasel.repository.ReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ModerationService {

    private final ReportRepository reportRepository;
    private final ModerationAuditService moderationAuditService;

    public ModerationService(ReportRepository reportRepository,
                             ModerationAuditService moderationAuditService) {
        this.reportRepository = reportRepository;
        this.moderationAuditService = moderationAuditService;
    }

    @Transactional
    public Map<String, Object> verifyReport(Long reportId, User moderator, String reason) {
        validateModerator(moderator);

        Report report = getReport(reportId);
        report.setStatus(ReportStatus.VERIFIED);
        report.setDuplicateOfReport(null);

        Report saved = reportRepository.save(report);
        moderationAuditService.log(saved, moderator, ModerationActionType.VERIFY, reason);

        return buildResponse(saved, "Report verified successfully.");
    }

    @Transactional
    public Map<String, Object> rejectReport(Long reportId, User moderator, String reason) {
        validateModerator(moderator);

        Report report = getReport(reportId);
        report.setStatus(ReportStatus.REJECTED);
        report.setDuplicateOfReport(null);

        Report saved = reportRepository.save(report);
        moderationAuditService.log(saved, moderator, ModerationActionType.REJECT, reason);

        return buildResponse(saved, "Report rejected successfully.");
    }

    @Transactional
    public Map<String, Object> markDuplicate(Long reportId, User moderator, Long duplicateOfReportId, String reason) {
        validateModerator(moderator);

        if (duplicateOfReportId == null) {
            throw new IllegalArgumentException("duplicateOfReportId is required.");
        }

        Report report = getReport(reportId);
        Report originalReport = getReport(duplicateOfReportId);

        if (report.getReportId().equals(originalReport.getReportId())) {
            throw new IllegalArgumentException("A report cannot be marked as duplicate of itself.");
        }

        report.setStatus(ReportStatus.DUPLICATE);
        report.setDuplicateOfReport(originalReport);

        Report saved = reportRepository.save(report);

        moderationAuditService.log(
                saved,
                moderator,
                ModerationActionType.MARK_DUPLICATE,
                reason != null ? reason : "Marked as duplicate of report " + duplicateOfReportId
        );

        return buildResponse(saved, "Report marked as duplicate successfully.");
    }

    private void validateModerator(User moderator) {
        if (moderator == null ||
                (moderator.getRole() != Role.MODERATOR && moderator.getRole() != Role.ADMIN)) {
            throw new UnauthorizedException("Only moderators or admins can perform moderation actions.");
        }
    }

    private Report getReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
    }

    private Map<String, Object> buildResponse(Report report, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("reportId", report.getReportId());
        response.put("status", report.getStatus());
        response.put("duplicateOfReportId",
                report.getDuplicateOfReport() != null ? report.getDuplicateOfReport().getReportId() : null);
        response.put("message", message);
        return response;
    }
}