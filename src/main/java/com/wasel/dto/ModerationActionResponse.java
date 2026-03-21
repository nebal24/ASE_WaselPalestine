package com.wasel.dto;

import com.wasel.model.ModerationActionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ModerationActionResponse {
    private Long actionId;
    private Long reportId;
    private Long performedByUserId;
    private ModerationActionType actionType;
    private String reason;
    private LocalDateTime createdAt;
}