package com.wasel.controller;

import com.wasel.entity.Checkpoint;
import com.wasel.entity.CheckpointStatusHistory;
import com.wasel.model.CheckpointStatus;
import com.wasel.service.CheckpointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/checkpoints")
@RequiredArgsConstructor
public class CheckpointController {

    private final CheckpointService checkpointService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Checkpoint> createCheckpoint(
            @RequestBody Checkpoint checkpoint,
            @RequestAttribute Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(checkpointService.createCheckpoint(checkpoint, userId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Checkpoint> updateStatus(
            @PathVariable Long id,
            @RequestParam CheckpointStatus status,
            @RequestAttribute Long userId) {
        return ResponseEntity.ok(checkpointService.updateStatus(id, status, userId));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<CheckpointStatusHistory>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(checkpointService.getStatusHistory(id));
    }
}