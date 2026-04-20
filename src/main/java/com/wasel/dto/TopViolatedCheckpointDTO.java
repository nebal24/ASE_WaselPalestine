package com.wasel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TopViolatedCheckpointDTO {
    private Long checkpointId;
    private String checkpointName;
    private Long incidentCount;
    private Double averageSeverity;
}
