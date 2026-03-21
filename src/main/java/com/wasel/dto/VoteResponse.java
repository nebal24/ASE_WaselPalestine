package com.wasel.dto;

import com.wasel.model.ReportStatus;
import com.wasel.model.VoteType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoteResponse {
    private Long reportId;
    private Long userId;
    private VoteType userVote;
    private long upvotes;
    private long downvotes;
    private long score;
    private ReportStatus reportStatus;
    private String message;
}