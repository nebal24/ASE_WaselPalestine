package com.wasel.controller;

import com.wasel.dto.AlertResponse;
import com.wasel.entity.User;
import com.wasel.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController
{
    private final AlertService alertService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<AlertResponse>> getMyAlerts(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(alertService.getMyAlerts(currentUser.getId()));
    }
}