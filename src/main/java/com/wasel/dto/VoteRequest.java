package com.wasel.dto;

import com.wasel.model.VoteType;
import lombok.Data;

@Data
public class VoteRequest {
    private VoteType voteType;
}