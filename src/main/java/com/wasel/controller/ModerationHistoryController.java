package com.wasel.controller;

import com.wasel.dto.ModerationActionResponse;
import com.wasel.service.ModerationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ModerationHistoryController {

    private final ModerationHistoryService moderationHistoryService;

    @GetMapping("/{reportId}/moderation-history")
    public ResponseEntity<List<ModerationActionResponse>> getHistory(@PathVariable Long reportId) {
        return ResponseEntity.ok(moderationHistoryService.getReportHistory(reportId));
    }
}