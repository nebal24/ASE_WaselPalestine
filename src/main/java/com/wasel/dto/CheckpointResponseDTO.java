package com.wasel.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckpointResponseDTO {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String currentStatus;
    private String description;
}
