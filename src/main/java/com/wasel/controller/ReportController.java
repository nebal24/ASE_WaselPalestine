package com.wasel.controller;
import com.wasel.dto.ReportRequestDTO;
import com.wasel.dto.ReportResponseDTO;
import com.wasel.entity.Report;
import com.wasel.service.ReportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//handles HTTP requests and returns JSON
@RestController

// Sets the base URL for all endpoints in this class
//يعني كل function داخل هذا الكلاس رح تبدأ بـ:
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final ReportService reportService;
    public ReportController(ReportService reportService) {this.reportService = reportService;}

    // Indicates this method handles POST requests (creating new data)
    @PostMapping
    //وظيفته:يستقبل البيانات  ، يبعثها للـ service ، يرجع النتيجة
    public ReportResponseDTO createReport(@RequestBody ReportRequestDTO ReportRequestDTO)
    {
        return reportService.createReportFromDTO(ReportRequestDTO);
    }
}
