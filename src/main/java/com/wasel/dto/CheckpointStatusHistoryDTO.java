package com.wasel.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CheckpointStatusHistoryDTO {
    private Long statusId;
    private String status;
    private LocalDateTime updatedAt;
    private Long updatedById;
    private String updatedByName;
}
