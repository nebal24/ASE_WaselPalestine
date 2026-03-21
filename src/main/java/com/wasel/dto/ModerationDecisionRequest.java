package com.wasel.dto;

import lombok.Data;

@Data
public class ModerationDecisionRequest {
    private String reason;
    private Long duplicateOfReportId; // used only if marking duplicate
}