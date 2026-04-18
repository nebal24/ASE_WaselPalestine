package com.wasel.dto;

import lombok.Data;

@Data
public class CheckpointRequestDTO {
    private String name;
    private Double latitude;
    private Double longitude;
    private String description;
}
