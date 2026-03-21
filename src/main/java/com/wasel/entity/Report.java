package com.wasel.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.wasel.model.*;
import org.hibernate.annotations.CreationTimestamp;

/*Created a Report class and connected it to the reports table in the database.*/
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "related_checkpoint_id", nullable = true)
    private Checkpoint checkpoint;

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
    private Status status;

    //Constructors, Getters, Setters
    public Report() {
        this.status = Status.PENDING;
    }
    public String getDescription() {return description;}
    public Category getCategory() {return category;}
    public Double  getLatitude() {return latitude;}
    public Double  getLongitude() {return longitude;}

    public void setDescription(String description) {this.description = description;}
    public void setCategory(Category category) {this.category = category;}
    public void setLatitude(Double latitude) {this.latitude = latitude;}
    public void setLongitude(Double longitude) {this.longitude = longitude;}
    public Integer getReportId()             { return reportId; }
    public Status getStatus()                { return status; }
    public LocalDateTime getTimestamp()      { return timestamp; }
    public void setStatus(Status status)     { this.status = status; }


    public void setCheckpoint(Checkpoint checkpoint) {this.checkpoint=checkpoint;}

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
