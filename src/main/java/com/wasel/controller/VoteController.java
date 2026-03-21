package com.wasel.controller;

import com.wasel.dto.VoteRequest;
import com.wasel.dto.VoteResponse;
import com.wasel.entity.User;
import com.wasel.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/{reportId}/votes")
    public ResponseEntity<VoteResponse> castVote(
            @PathVariable Long reportId,
            @RequestBody VoteRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        VoteResponse response = voteService.castOrUpdateVote(
                reportId,
                currentUser,
                request.getVoteType()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reportId}/votes")
    public ResponseEntity<VoteResponse> removeVote(
            @PathVariable Long reportId,
            @AuthenticationPrincipal User currentUser
    ) {
        VoteResponse response = voteService.removeVote(reportId, currentUser);
        return ResponseEntity.ok(response);
    }
}