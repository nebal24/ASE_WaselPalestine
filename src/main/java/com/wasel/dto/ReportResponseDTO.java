package com.wasel.dto;
import com.wasel.model.Status;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportResponseDTO {

    // For success responses
    private Integer reportId;
    private Status status;
    private LocalDateTime timestamp;
    private String message;
    // For error responses
    private List<String> errors;

    // Success constructor
    public ReportResponseDTO(Integer reportId, Status status, LocalDateTime timestamp, String message) {
        this.reportId  = reportId;
        this.status    = status;
        this.timestamp = timestamp;
        this.message   = message;
    }
    // Error constructor
    public ReportResponseDTO(List<String> errors) {this.errors = errors;}

    public Integer getReportId()            { return reportId; }
    public void setReportId(Integer id)     { this.reportId = id; }
    public Status getStatus()               { return status; }
    public void setStatus(Status status)    { this.status = status; }
    public LocalDateTime getTimestamp()              { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp){ this.timestamp = timestamp; }
    public String getMessage()              { return message; }
    public void setMessage(String message)  { this.message = message; }
    public List<String> getErrors()         { return errors; }
    public void setErrors(List<String> e)   { this.errors = e; }
}