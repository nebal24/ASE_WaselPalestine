package com.wasel.util;

import org.springframework.stereotype.Component;

/**
 * Utility class for calculating distances using the Haversine formula
 * Used as fallback when external API is unavailable
 */
@Component
public class HaversineCalculator {

    /** Earth's radius in kilometers */
    private static final double EARTH_RADIUS_KM = 6371;

    /**
     * Calculate straight-line distance between two points using Haversine formula
     *
     * @param lat1 Starting point latitude
     * @param lon1 Starting point longitude
     * @param lat2 Destination latitude
     * @param lon2 Destination longitude
     * @return Distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert degrees to radians
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // Haversine formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Estimate travel time based on distance and average speed
     *
     * @param distanceKm Distance in kilometers
     * @param averageSpeedKmh Average speed in km/h (default 50 km/h)
     * @return Estimated duration in minutes
     */
    public int estimateDuration(double distanceKm, double averageSpeedKmh) {
        double speed = averageSpeedKmh > 0 ? averageSpeedKmh : 50;  // Default 50 km/h
        return (int) ((distanceKm / speed) * 60);  // Convert hours to minutes
    }
}