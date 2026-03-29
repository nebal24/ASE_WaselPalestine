package com.wasel.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.wasel.model.*;
import org.hibernate.annotations.CreationTimestamp;

/*Created a Report class and connected it to the reports table in the database.*/
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    // WHO created the report
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

    // duplicate relation (IMPORTANT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "duplicate_of_report_id")
    private Report duplicateOfReport;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModerationAction> moderationActions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "related_checkpoint_id", nullable = true)
    private Checkpoint checkpoint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentCategory category;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status= ReportStatus.PENDING;


    //Constructors, Getters, Setters
    public Report() {
        this.status = ReportStatus.PENDING;
    }


    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public IncidentCategory getCategory() {return category;}
    public void setCategory(IncidentCategory category) {this.category = category;}

    public Double  getLatitude() {return latitude;}
    public void setLatitude(Double latitude) {this.latitude = latitude;}

    public Double  getLongitude() {return longitude;}
    public void setLongitude(Double longitude) {this.longitude = longitude;}

    public ReportStatus getStatus() {return status;}
    public void setStatus(ReportStatus status) {this.status = status;}

    public Report getDuplicateOfReport() {return duplicateOfReport;}
    public void setDuplicateOfReport(Report duplicateOfReport) {this.duplicateOfReport = duplicateOfReport;}

    public LocalDateTime getTimestamp() {return timestamp;}
    public void setCheckpoint(Checkpoint checkpoint) {this.checkpoint=checkpoint;}
    public void setCreatedBy(User user) {this.createdBy = user;}
    public Long getReportId() {return reportId;}


}
