package com.wasel.repository;

import com.wasel.entity.AlertSubscription;
import com.wasel.model.IncidentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertSubscriptionRepository extends JpaRepository<AlertSubscription, Long> {

    List<AlertSubscription> findByUserIdOrderByCreatedAtDesc(Long userId);

    //finds all active subscriptions that match the incident's category AND location

    @Query("""
        SELECT s FROM AlertSubscription s
        WHERE s.active = true
        AND s.category = :category
        AND (6371 * acos(
            cos(radians(:lat)) * cos(radians(s.centerLatitude)) *
            cos(radians(s.centerLongitude) - radians(:lon)) +
            sin(radians(:lat)) * sin(radians(s.centerLatitude))
        )) <= s.radiusKm
    """)
    List<AlertSubscription> findActiveMatchingSubscriptions(
            @Param("category") IncidentCategory category,
            @Param("lat") double lat,
            @Param("lon") double lon
    );
}