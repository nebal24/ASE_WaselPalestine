package com.wasel.controller;
import com.wasel.dto.PagedReportResponse;
import com.wasel.dto.ReportRequestDTO;
import com.wasel.dto.ReportResponseDTO;
import com.wasel.dto.ReportSummaryDTO;
import com.wasel.entity.Report;
import com.wasel.model.IncidentCategory;
import com.wasel.model.ReportStatus;
import com.wasel.service.ReportService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//handles HTTP requests and returns JSON
@RestController

// Sets the base URL for all endpoints in this class
//يعني كل function داخل هذا الكلاس رح تبدأ بـ:
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final ReportService reportService;
    public ReportController(ReportService reportService) {this.reportService = reportService;}

    // Indicates this method handles POST requests (creating new data)
    //وظيفته:يستقبل البيانات  ، يبعثها للـ service ، يرجع النتيجة
    @PostMapping
    public ResponseEntity<ReportResponseDTO> createReport(@RequestBody ReportRequestDTO reportRequestDTO)
    {
        ReportResponseDTO response = reportService.createReportFromDTO(reportRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all reports with optional filters for status and category.
     * Supports pagination and returns a clean structured response.
     */
    @GetMapping
    public ResponseEntity<PagedReportResponse> getAllReports(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) IncidentCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reportService.getAllReports(status, category, pageable));
    }

    /**
     * Get a single report by its ID.
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportSummaryDTO> getReportById(@PathVariable Long reportId) {
        return ResponseEntity.ok(reportService.getReportById(reportId));
    }
}
