package com.wasel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserStatsByRoleDTO {
    private String role;
    private Long userCount;
    private Long totalSubmissions;
    private Long totalReports;
    private Long totalModerations;
}
