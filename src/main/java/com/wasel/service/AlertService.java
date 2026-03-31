package com.wasel.service;

import com.wasel.dto.AlertResponse;
import com.wasel.dto.WeatherResponse;
import com.wasel.entity.Alert;
import com.wasel.entity.AlertSubscription;
import com.wasel.entity.Incident;
import com.wasel.model.AlertStatus;
import com.wasel.repository.AlertRepository;
import com.wasel.repository.AlertSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertSubscriptionRepository alertSubscriptionRepository;
    private final NotificationService notificationService;
    private final WeatherService weatherService;

    public void triggerAlertsForIncident(Incident incident)
    {

        // Step 1: find all active subscriptions that match
        // this incident's category and location
        List<AlertSubscription> matchingSubscriptions =
                alertSubscriptionRepository.findActiveMatchingSubscriptions(
                        incident.getCategory(),
                        incident.getLatitude(),
                        incident.getLongitude()
                );

        // Step 2: for each matching subscription,
        // create one alert record for that user
        for (AlertSubscription subscription : matchingSubscriptions)
        {
            Alert alert = new Alert();
            alert.setUser(subscription.getUser());
            alert.setIncident(incident);
            alert.setStatus(AlertStatus.PENDING);

            alertRepository.save(alert);

            // Step 3: hand it off to the notification service
            // right now this does nothing — but in the future
            // it will send email, SMS, push, etc.
            notificationService.send(alert);
        }
    }

    public List<AlertResponse> getMyAlerts(Long userId)
    {
        return alertRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(alert -> {
                    // Fetch weather for the incident's location
                    WeatherResponse weather = weatherService.getWeather(
                            alert.getIncident().getLatitude(),
                            alert.getIncident().getLongitude()
                    );
                    return new AlertResponse(

                            alert.getId(),
                            alert.getIncident().getIncidentId(),
                            alert.getIncident().getCategory(),
                            alert.getIncident().getLatitude(),
                            alert.getIncident().getLongitude(),
                            alert.getStatus(),
                            alert.getCreatedAt(),
                            weather
                    );
                })
                .toList();
    }
}