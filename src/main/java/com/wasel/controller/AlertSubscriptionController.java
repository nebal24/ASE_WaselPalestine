package com.wasel.controller;

import com.wasel.dto.AlertSubscriptionRequest;
import com.wasel.dto.AlertSubscriptionResponse;
import com.wasel.entity.User;
import com.wasel.service.AlertSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alert-subscriptions")
@RequiredArgsConstructor
public class AlertSubscriptionController {

    private final AlertSubscriptionService alertSubscriptionService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<AlertSubscriptionResponse> createSubscription(
            @RequestBody AlertSubscriptionRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return new ResponseEntity<>(
                alertSubscriptionService.createSubscription(request, currentUser.getId()),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<AlertSubscriptionResponse>> getMySubscriptions(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                alertSubscriptionService.getMySubscriptions(currentUser.getId())
        );
    }

    @PutMapping("/{subscriptionId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<AlertSubscriptionResponse> updateSubscription(
            @PathVariable Long subscriptionId,
            @RequestBody AlertSubscriptionRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                alertSubscriptionService.updateSubscription(subscriptionId, request, currentUser.getId())
        );
    }

    @DeleteMapping("/{subscriptionId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSubscription(
            @PathVariable Long subscriptionId,
            @AuthenticationPrincipal User currentUser
    ) {
        alertSubscriptionService.deleteSubscription(subscriptionId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}