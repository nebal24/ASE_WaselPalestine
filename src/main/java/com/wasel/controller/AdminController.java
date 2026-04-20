package com.wasel.controller;

import com.wasel.dto.ReportSummaryDTO;
import com.wasel.dto.TopViolatedCheckpointDTO;
import com.wasel.dto.UserStatsByRoleDTO;
import com.wasel.model.IncidentCategory;
import com.wasel.service.AdminService;
import com.wasel.service.CheckpointService;
import com.wasel.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CheckpointService checkpointService;
    private final ReportService reportService;
    private final AdminService adminService;

    /**
     * GET /api/v1/admin/checkpoints/top-violated
     *
     * Returns all checkpoints ranked by incident frequency.
     * Uses native SQL with LEFT JOIN + GROUP BY + CASE severity scoring.
     */
    @GetMapping("/checkpoints/top-violated")
    public ResponseEntity<List<TopViolatedCheckpointDTO>> getTopViolatedCheckpoints() {
        return ResponseEntity.ok(checkpointService.getTopViolatedCheckpoints());
    }

    /**
     * GET /api/v1/admin/reports/nearby
     *
     * Returns reports within {@code radius} km of the given coordinates,
     * in the given category, submitted within {@code minutes} minutes.
     * Uses native SQL with the Haversine great-circle distance formula.
     *
     * @param lat      centre latitude
     * @param lng      centre longitude
     * @param radius   search radius in kilometres (default 1.0)
     * @param category incident category filter (e.g. ACCIDENT)
     * @param minutes  look-back window in minutes (default 60)
     */
    @GetMapping("/reports/nearby")
    public ResponseEntity<List<ReportSummaryDTO>> getNearbyReports(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1.0") double radius,
            @RequestParam IncidentCategory category,
            @RequestParam(defaultValue = "60") int minutes) {

        return ResponseEntity.ok(
                reportService.findNearbyDuplicateReports(lat, lng, radius, category, minutes));
    }

    /**
     * GET /api/v1/admin/users/stats-by-role
     *
     * Returns aggregated activity counts grouped by user role.
     * Uses native SQL with COUNT(DISTINCT ...) to avoid fan-out from multiple JOINs.
     */
    @GetMapping("/users/stats-by-role")
    public ResponseEntity<List<UserStatsByRoleDTO>> getUserStatsByRole() {
        return ResponseEntity.ok(adminService.getUserStatsByRole());
    }
}
