package com.wasel.service;
import com.wasel.dto.ReportRequestDTO;
import com.wasel.dto.ReportResponseDTO;
import com.wasel.entity.Checkpoint;
import com.wasel.entity.Report;
import com.wasel.entity.User;
import com.wasel.exception.ResourceNotFoundException;
import com.wasel.repository.CheckpointRepository;
import com.wasel.exception.ValidationException;
import com.wasel.model.Category;
import com.wasel.model.Status;
import com.wasel.repository.ReportRepository;
import com.wasel.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final CheckpointRepository checkpointRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, CheckpointRepository checkpointRepository , UserRepository userRepository  )
    {
        this.reportRepository = reportRepository;
        this.checkpointRepository = checkpointRepository;
        this.userRepository = userRepository;
    }

    public ReportResponseDTO createReportFromDTO(ReportRequestDTO dto)
    {
        // 1. Validate input & convert category string to enum
        Category categoryEnum = validateReportDTO(dto);

        // 2. Check abuse
        checkAbuse(dto.getUserId(), dto.getLatitude(), dto.getLongitude());

        // 3. Create & save report
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

    private ReportResponseDTO createAndSaveReport(ReportRequestDTO dto, Category categoryEnum) {
//        // GET THE LOGGED-IN USER FROM THE JWT TOKEN
//        User currentUser = (User) SecurityContextHolder
//                .getContext()
//                .getAuthentication()
//                .getPrincipal()userRepository;

        Report report = new Report();
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        report.setUser(user);
        report.setDescription(dto.getDescription());
        report.setLatitude(dto.getLatitude());
        report.setLongitude(dto.getLongitude());
        report.setCategory(categoryEnum);
//        report.setUser(currentUser);

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

        // save() returns the saved entity WITH the generated ID and timestamp
        Report saved = reportRepository.save(report);

        return new ReportResponseDTO(
                saved.getReportId(),
                saved.getStatus(),
                saved.getTimestamp(),
                "Report submitted successfully and is now in Unverified Reports"
        );
    }
}


