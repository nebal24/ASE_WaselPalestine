package com.wasel.dto;
import com.wasel.model.ReportStatus;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportResponseDTO {

    // For success responses
    private Long reportId;
    private ReportStatus status;
    private LocalDateTime timestamp;
    private String message;
    // For error responses
    private List<String> errors;

    // Success constructor
    public ReportResponseDTO(Long reportId, ReportStatus status, LocalDateTime timestamp, String message) {
        this.reportId  = reportId;
        this.status    = status;
        this.timestamp = timestamp;
        this.message   = message;
    }
    // Error constructor
    public ReportResponseDTO(List<String> errors) {this.errors = errors;}

    public Long getReportId()            { return reportId; }
    public void setReportId(Long id)     { this.reportId = id; }
    public ReportStatus getStatus()               { return status; }
    public void setStatus(ReportStatus status)    { this.status = status; }
    public LocalDateTime getTimestamp()              { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp){ this.timestamp = timestamp; }
    public String getMessage()              { return message; }
    public void setMessage(String message)  { this.message = message; }
    public List<String> getErrors()         { return errors; }
    public void setErrors(List<String> e)   { this.errors = e; }
}