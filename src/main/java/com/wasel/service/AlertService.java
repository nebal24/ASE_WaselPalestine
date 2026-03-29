package com.wasel.service;

import com.wasel.dto.AlertResponse;
import com.wasel.entity.Alert;
import com.wasel.entity.AlertSubscription;
import com.wasel.entity.Incident;
import com.wasel.repository.AlertRepository;
import com.wasel.repository.AlertSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to check active alert subscriptions against verified incidents.
 * Currently logs matches; notification sending will be integrated later.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertSubscriptionRepository subscriptionRepository;
    private final AlertRepository alertRepository;

    /**
     * Check active subscriptions and log those matching the incident by category and proximity.
     */
    public void triggerAlertsForIncident(Incident incident) {
        if (incident == null) return;

        // Use repository query that checks category and geographic proximity
        List<AlertSubscription> matches = subscriptionRepository.findActiveMatchingSubscriptions(
                incident.getCategory(),
                incident.getLatitude(),
                incident.getLongitude()
        );

        if (matches == null || matches.isEmpty()) {
            log.info("No alert subscriptions match incident={}", incident.getIncidentId());
            return;
        }

        for (AlertSubscription sub : matches) {
            log.info("Alert match: subscriptionId={} userId={} incidentId={}",
                    sub.getId(), sub.getUser() != null ? sub.getUser().getId() : null,
                    incident.getIncidentId());
        }
    }

    /**
     * Return alerts for a given user, newest first
     */
    public List<AlertResponse> getMyAlerts(Long userId) {
        List<Alert> alerts = alertRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<AlertResponse> result = new ArrayList<>();
        if (alerts == null) return result;
        for (Alert a : alerts) {
            AlertResponse r = new AlertResponse();
            r.setId(a.getId());
            r.setIncidentId(a.getIncident() != null ? a.getIncident().getIncidentId() : null);
            r.setIncidentCategory(a.getIncident() != null ? a.getIncident().getCategory() : null);
            r.setIncidentLatitude(a.getIncident() != null ? a.getIncident().getLatitude() : null);
            r.setIncidentLongitude(a.getIncident() != null ? a.getIncident().getLongitude() : null);
            r.setStatus(a.getStatus());
            r.setCreatedAt(a.getCreatedAt());
            r.setWeather(null);
            result.add(r);
        }
        return result;
    }
}
