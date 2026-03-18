package com.wasel.service;
import com.wasel.dto.ReportRequestDTO;
import com.wasel.dto.ReportResponseDTO;
import com.wasel.entity.Report;
import com.wasel.exception.ValidationException;
import com.wasel.model.Category;
import com.wasel.model.Status;
import com.wasel.repository.ReportRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    public ReportService(ReportRepository reportRepository) {this.reportRepository = reportRepository;}

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

    private ReportResponseDTO createAndSaveReport(ReportRequestDTO dto, Category categoryEnum)
    {
        Report report = new Report();
        report.setDescription(dto.getDescription());
        report.setLatitude(dto.getLatitude());
        report.setLongitude(dto.getLongitude());
        report.setCategory(categoryEnum);

        reportRepository.save(report);

        return new ReportResponseDTO(List.of("Report submitted successfully and is now in Unverified Reports"));
    }
}


