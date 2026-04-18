package com.wasel.controller;

import com.wasel.dto.CheckpointRequestDTO;
import com.wasel.dto.CheckpointResponseDTO;
import com.wasel.dto.CheckpointStatusHistoryDTO;
import com.wasel.entity.User;
import com.wasel.model.CheckpointStatus;
import com.wasel.service.CheckpointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/checkpoints")
@RequiredArgsConstructor
public class CheckpointController {

    private final CheckpointService checkpointService;

    @GetMapping
    public ResponseEntity<List<CheckpointResponseDTO>> getAllCheckpoints() {
        return ResponseEntity.ok(checkpointService.getAllCheckpoints());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckpointResponseDTO> getCheckpointById(@PathVariable Long id) {
        return ResponseEntity.ok(checkpointService.getCheckpointById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CheckpointResponseDTO> createCheckpoint(
            @RequestBody CheckpointRequestDTO request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(checkpointService.createCheckpoint(request, user.getId()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<CheckpointResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam CheckpointStatus status,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(checkpointService.updateStatus(id, status, user.getId()));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<CheckpointStatusHistoryDTO>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(checkpointService.getStatusHistory(id));
    }
}