package com.wasel.controller;

import com.wasel.dto.ModerationDecisionRequest;
import com.wasel.entity.User;
import com.wasel.service.ModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
public class ModerationController {

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @PostMapping("/{reportId}/moderation/verify")
    public ResponseEntity<Map<String, Object>> verifyReport(
            @PathVariable Long reportId,
            @RequestBody ModerationDecisionRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                moderationService.verifyReport(reportId, currentUser, request.getReason())
        );
    }

    @PostMapping("/{reportId}/moderation/reject")
    public ResponseEntity<Map<String, Object>> rejectReport(
            @PathVariable Long reportId,
            @RequestBody ModerationDecisionRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                moderationService.rejectReport(reportId, currentUser, request.getReason())
        );
    }

    @PostMapping("/{reportId}/moderation/duplicate")
    public ResponseEntity<Map<String, Object>> markDuplicate(
            @PathVariable Long reportId,
            @RequestBody ModerationDecisionRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                moderationService.markDuplicate(
                        reportId,
                        currentUser,
                        request.getDuplicateOfReportId(),
                        request.getReason()
                )
        );
    }
}