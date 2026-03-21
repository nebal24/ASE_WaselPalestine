package com.wasel.dto;

// Container for user input data when creating a report
public class ReportRequestDTO {
    private String description;
    private String  category;
    private Double latitude;
    private Double longitude;
    private Long relatedCheckpointId;   // optional - ممكن يكون null

    public Long getRelatedCheckpointId() { return relatedCheckpointId; }
    public void setRelatedCheckpointId(Long id) { this.relatedCheckpointId = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String  getCategory() { return category; }
    public void setCategory(String  category) { this.category = category; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

}