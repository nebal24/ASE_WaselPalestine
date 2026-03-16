package com.wasel.dto;

import lombok.Data;

@Data
public class CheckpointSummaryDTO {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String currentStatus;
}