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
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name = "related_checkpoint_id", nullable = true)
//    private Checkpoint checkpoint;
//
//    @ManyToOne
//    @JoinColumn(name = "related_incident_id", nullable = false)
//    private Incident incident;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

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
    public Category getCategory() {return category;}
    public Double  getLatitude() {return latitude;}
    public Double  getLongitude() {return longitude;}
    public ReportStatus getStatus() {return status;}
    public LocalDateTime getTimestamp() {return timestamp;}
    public void setStatus(ReportStatus status) {this.status = status;}
    public void setDescription(String description) {this.description = description;}
    public void setCategory(Category category) {this.category = category;}
    public void setLatitude(Double latitude) {this.latitude = latitude;}
    public void setLongitude(Double longitude) {this.longitude = longitude;}
    public void setCreatedBy(User user) {this.createdBy = user;}
    public Report getDuplicateOfReport() {return duplicateOfReport;}
    public void setDuplicateOfReport(Report duplicateOfReport) {this.duplicateOfReport = duplicateOfReport;}

    public Long getReportId() {return reportId;}
    // public void setUser(User mockUser) {this.user=mockUser;}
}
