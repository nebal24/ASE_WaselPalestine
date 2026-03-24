package com.wasel.service;

import com.wasel.dto.AlertSubscriptionRequest;
import com.wasel.dto.AlertSubscriptionResponse;
import com.wasel.entity.AlertSubscription;
import com.wasel.entity.User;
import com.wasel.exception.BadRequestException;
import com.wasel.exception.ResourceNotFoundException;
import com.wasel.exception.UnauthorizedException;
import com.wasel.repository.AlertSubscriptionRepository;
import com.wasel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertSubscriptionService {

    private final AlertSubscriptionRepository alertSubscriptionRepository;
    private final UserRepository userRepository;
    private final GeocodingService geocodingService;
    public AlertSubscriptionResponse createSubscription(AlertSubscriptionRequest request, Long userId) {
        validateRequest(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        double[] coords = geocodingService.getCoordinatesFromPlace(request.getPlaceName());
        Double radius = request.getRadiusKm() != null ? request.getRadiusKm() : 5.0;

        AlertSubscription subscription = new AlertSubscription();
        subscription.setUser(user);
        subscription.setPlaceName(cleanPlaceName(request.getPlaceName()));
        subscription.setCenterLatitude(coords[0]);
        subscription.setCenterLongitude(coords[1]);
        subscription.setRadiusKm(radius);
        subscription.setCategory(request.getCategory());
        subscription.setActive(request.getActive() != null ? request.getActive() : true);

        AlertSubscription saved = alertSubscriptionRepository.save(subscription);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AlertSubscriptionResponse> getMySubscriptions(Long userId) {
        return alertSubscriptionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AlertSubscriptionResponse updateSubscription(Long subscriptionId, AlertSubscriptionRequest request, Long userId) {
        validateRequest(request);

        AlertSubscription subscription = alertSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert subscription not found"));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You can only update your own subscriptions");
        }

        double[] coords = geocodingService.getCoordinatesFromPlace(request.getPlaceName());
        Double radius = request.getRadiusKm() != null ? request.getRadiusKm() : 5.0;

        subscription.setPlaceName(cleanPlaceName(request.getPlaceName()));
        subscription.setCenterLatitude(coords[0]);
        subscription.setCenterLongitude(coords[1]);
        subscription.setRadiusKm(radius);
        subscription.setCategory(request.getCategory());
        subscription.setActive(request.getActive() != null ? request.getActive() : subscription.getActive());

        AlertSubscription updated = alertSubscriptionRepository.save(subscription);
        return mapToResponse(updated);
    }
    public void deleteSubscription(Long subscriptionId, Long userId) {
        AlertSubscription subscription = alertSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert subscription not found"));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own subscriptions");
        }

        alertSubscriptionRepository.delete(subscription);
    }

    private void validateRequest(AlertSubscriptionRequest request) {
        if (request.getCategory() == null) {
            throw new BadRequestException("Incident category is required");
        }

        if (request.getPlaceName() == null || request.getPlaceName().isBlank()) {
            throw new BadRequestException("Place name is required");
        }

        if (request.getRadiusKm() != null && request.getRadiusKm() <= 0) {
            throw new BadRequestException("Radius must be greater than 0");
        }
    }

    private String cleanPlaceName(String placeName) {
        if (placeName == null) {
            return null;
        }

        String cleaned = placeName.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }

    private AlertSubscriptionResponse mapToResponse(AlertSubscription subscription) {
        return new AlertSubscriptionResponse(
                subscription.getId(),
                subscription.getUser().getId(),
                subscription.getPlaceName(),
                subscription.getCenterLatitude(),
                subscription.getCenterLongitude(),
                subscription.getRadiusKm(),
                subscription.getCategory(),
                subscription.getActive(),
                subscription.getCreatedAt(),
                subscription.getUpdatedAt()
        );
    }
}