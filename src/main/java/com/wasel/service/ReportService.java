package com.wasel.service;

import com.wasel.dto.ReportRequestDTO;
import com.wasel.dto.ReportResponseDTO;
import com.wasel.entity.Report;
import com.wasel.entity.User;
import com.wasel.exception.ValidationException;
import com.wasel.model.Category;
import com.wasel.model.ModerationActionType;
import com.wasel.model.ReportStatus;
import com.wasel.repository.ReportRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final ModerationAuditService moderationAuditService;

    public ReportService(ReportRepository reportRepository,
                         ModerationAuditService moderationAuditService) {
        this.reportRepository = reportRepository;
        this.moderationAuditService = moderationAuditService;
    }
    public ReportResponseDTO createReportFromDTO(ReportRequestDTO dto)
    {
        // 1. Validate input & convert category string to enum
        Category categoryEnum = validateReportDTO(dto);

        // 2. Create & save report
        return createAndSaveReport(dto, categoryEnum);
    }

    private Category validateReportDTO(ReportRequestDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validate description
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            errors.add("Description cannot be empty");
        } else if (dto.getDescription().length() < 10) {
            errors.add("Description must be at least 10 characters");
        } else if (dto.getDescription().length() > 300) {
            errors.add("Description cannot exceed 300 characters");
        }

        // Validate location
        if (dto.getLatitude() == null) {
            errors.add("Latitude must be provided");
        } else if (dto.getLatitude() < 31 || dto.getLatitude() > 32.5) {
            errors.add("Latitude must be between 31.0 and 32.5 for Palestine");
        }

        if (dto.getLongitude() == null) {
            errors.add("Longitude must be provided");
        } else if (dto.getLongitude() < 34 || dto.getLongitude() > 35.5) {
            errors.add("Longitude must be between 34.0 and 35.5 for Palestine");
        }

        // Validate category
        Category categoryEnum = null;
        try {
            categoryEnum = Category.valueOf(dto.getCategory());
        } catch (Exception e) {
            errors.add("Category must be one of [ACCIDENT, DELAY, WEATHER_HAZARD, CLOSURE]");
        }

        // Throw if errors exist
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return categoryEnum;
    }

    private ReportResponseDTO createAndSaveReport(ReportRequestDTO dto, Category categoryEnum) {
        User currentUser = getCurrentUser();

        Report report = new Report();
        report.setCreatedBy(currentUser);
        report.setDescription(dto.getDescription());
        report.setLatitude(dto.getLatitude());
        report.setLongitude(dto.getLongitude());
        report.setCategory(categoryEnum);

        handleDuplicateDetection(report);

        Report savedReport = reportRepository.save(report);

        if (savedReport.getStatus() == ReportStatus.DUPLICATE
                && savedReport.getDuplicateOfReport() != null) {
            moderationAuditService.log(
                    savedReport,
                    currentUser,
                    ModerationActionType.AUTO_MARK_DUPLICATE,
                    "Automatically marked as duplicate of report #" +
                            savedReport.getDuplicateOfReport().getReportId()
            );
        }

        return new ReportResponseDTO(List.of(
                savedReport.getStatus() == ReportStatus.DUPLICATE
                        ? "Report submitted but marked as duplicate"
                        : "Report submitted successfully and is now in Unverified Reports"
        ));
    }
    private void handleDuplicateDetection(Report report) {
        double range = 0.005; // about 500m
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);

        List<Report> nearbyReports =
                reportRepository.findByCategoryAndStatusInAndLatitudeBetweenAndLongitudeBetweenAndTimestampAfter(
                        report.getCategory(),
                        List.of(ReportStatus.PENDING, ReportStatus.VERIFIED),
                        report.getLatitude() - range,
                        report.getLatitude() + range,
                        report.getLongitude() - range,
                        report.getLongitude() + range,
                        threshold
                );

        if (!nearbyReports.isEmpty()) {
            Report originalReport = nearbyReports.get(0);
            report.setStatus(ReportStatus.DUPLICATE);
            report.setDuplicateOfReport(originalReport);
            return;
        }

        report.setStatus(ReportStatus.PENDING);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}


