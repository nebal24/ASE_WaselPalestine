package com.wasel.service;
import com.wasel.dto.PagedReportResponse;
import com.wasel.dto.ReportRequestDTO;
import com.wasel.dto.ReportResponseDTO;
import com.wasel.dto.ReportSummaryDTO;
import com.wasel.entity.Checkpoint;
import com.wasel.entity.Report;
import com.wasel.entity.User;
import com.wasel.exception.ResourceNotFoundException;
import com.wasel.exception.ValidationException;
import com.wasel.model.IncidentCategory;
import com.wasel.model.ModerationActionType;
import com.wasel.model.ReportStatus;
import com.wasel.repository.CheckpointRepository;
import com.wasel.repository.ReportRepository;
import com.wasel.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final CheckpointRepository checkpointRepository;
    private final UserRepository userRepository;
    private final ModerationAuditService moderationAuditService;

    public ReportService(ReportRepository reportRepository, CheckpointRepository checkpointRepository
            , UserRepository userRepository ,  ModerationAuditService moderationAuditService )
    {
        this.reportRepository = reportRepository;
        this.checkpointRepository = checkpointRepository;
        this.userRepository = userRepository;
        this.moderationAuditService = moderationAuditService;
    }

    public ReportResponseDTO createReportFromDTO(ReportRequestDTO dto)
    {
        // 1. Validate input & convert category string to enum
        IncidentCategory categoryEnum = validateReportDTO(dto);

        // 2. Check abuse
        User currentUser = getCurrentUser(); // Get user from token
        checkAbuse(currentUser.getId(), dto.getLatitude(), dto.getLongitude());

        // 3. Create & save report
        return createAndSaveReport(dto, categoryEnum , currentUser);
    }

    private IncidentCategory validateReportDTO(ReportRequestDTO dto)
    {
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
        IncidentCategory categoryEnum = null;
        try {
            categoryEnum = IncidentCategory.valueOf(dto.getCategory());
        } catch (Exception e) {
            errors.add("Category must be one of [ACCIDENT, DELAY, WEATHER_HAZARD, CLOSURE]");
        }

        // Throw if errors exist
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return categoryEnum;
    }

    private void checkAbuse(Long userId , Double latitude, Double longitude)
    {
        List<String> errors = new ArrayList<>();
        // Rule 1: max 5 reports per hour
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long hourlyCount = reportRepository.countRecentByUser(userId, oneHourAgo);
        if (hourlyCount >= 5) {errors.add("You have reached the maximum of 5 reports per hour");}

        // Rule 2: cooldown 2 minutes between reports
        LocalDateTime twoMinutesAgo = LocalDateTime.now().minusMinutes(2);
        long recentCount = reportRepository.countRecentByUser(userId, twoMinutesAgo);
        if (recentCount > 0) {errors.add("You must wait 2 minutes before submitting another report");}

        // Rule 3: max 3 reports from same location in 5 minutes
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        long nearbyCount = reportRepository.countNearbyRecentByUser(
                userId, fiveMinutesAgo, latitude, longitude);
        if (nearbyCount >= 3) {
            errors.add("You have submitted too many reports from the same location");
        }

        if (!errors.isEmpty()) {throw new ValidationException(errors);}
    }

    private ReportResponseDTO createAndSaveReport(ReportRequestDTO dto, IncidentCategory categoryEnum ,  User currentUser) {
        Report report = new Report();
        report.setCreatedBy(currentUser);
        report.setDescription(dto.getDescription());
        report.setLatitude(dto.getLatitude());
        report.setLongitude(dto.getLongitude());
        report.setCategory(categoryEnum);

        // ربط الـ checkpoint لو المواطن بعث ID
        if (dto.getRelatedCheckpointId() != null)
        {
            Checkpoint checkpoint = checkpointRepository
                    .findById(dto.getRelatedCheckpointId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Checkpoint with ID " + dto.getRelatedCheckpointId() + " not found"
                    ));
            report.setCheckpoint(checkpoint);
        }

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

        String message = savedReport.getStatus() == ReportStatus.DUPLICATE
                ? "Report submitted but marked as duplicate of report #" +
                savedReport.getDuplicateOfReport().getReportId()
                : "Report submitted successfully and is now in Unverified Reports";

        return new ReportResponseDTO(
                savedReport.getReportId(),
                savedReport.getStatus(),
                savedReport.getTimestamp(),
                message
        );
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

    private User getCurrentUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();

    }

    /**
     * Retrieves all reports with optional filters for status and category.
     * Returns a clean paginated response with only essential metadata.
     */
    public PagedReportResponse getAllReports(ReportStatus status, IncidentCategory category, Pageable pageable) {
        Page<Report> reports = reportRepository.findAllWithFilters(status, category, pageable);

        List<ReportSummaryDTO> reportList = reports.getContent().stream()
                .map(report -> new ReportSummaryDTO(
                        report.getReportId(),
                        report.getCategory(),
                        report.getDescription(),
                        report.getLatitude(),
                        report.getLongitude(),
                        report.getStatus(),
                        report.getTimestamp()
                ))
                .collect(Collectors.toList());

        return new PagedReportResponse(
                reportList,
                reports.getNumber(),        // current page
                reports.getTotalPages(),    // total pages
                reports.getTotalElements(), // total reports
                reports.getSize()           // page size
        );
    }

    /**
     * Query 2 — Nearby Duplicate Reports.
     *
     * Delegates to the native Haversine query in ReportRepository and converts
     * the returned Report entities to summary DTOs safe for serialization.
     */
    public List<ReportSummaryDTO> findNearbyDuplicateReports(
            double latitude, double longitude, double radiusKm,
            IncidentCategory category, int withinMinutes) {

        return reportRepository
                .findNearbyReports(latitude, longitude, radiusKm, category.name(), withinMinutes)
                .stream()
                .map(r -> new ReportSummaryDTO(
                        r.getReportId(),
                        r.getCategory(),
                        r.getDescription(),
                        r.getLatitude(),
                        r.getLongitude(),
                        r.getStatus(),
                        r.getTimestamp()))
                .toList();
    }

    /**
     * Retrieves a single report by its ID.
     */
    public ReportSummaryDTO getReportById(Long reportId)
    {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Report not found with id: " + reportId));
        return new ReportSummaryDTO(
                report.getReportId(),
                report.getCategory(),
                report.getDescription(),
                report.getLatitude(),
                report.getLongitude(),
                report.getStatus(),
                report.getTimestamp()
        );
    }
}


