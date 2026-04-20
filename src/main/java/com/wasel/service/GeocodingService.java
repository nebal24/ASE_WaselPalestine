package com.wasel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final RestTemplate restTemplate;

    // Simple in-memory cache: normalized place name -> [lat, lon]
    private final Map<String, double[]> coordinatesCache = new ConcurrentHashMap<>();

    public double[] getCoordinatesFromPlace(String placeName) {
        String normalizedPlace = normalizePlaceName(placeName);

        // Return cached coordinates if already resolved before
        double[] cached = coordinatesCache.get(normalizedPlace);
        if (cached != null) {
            return cached;
        }

        try {
            String encodedPlace = URLEncoder.encode(normalizedPlace, StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?q="
                    + encodedPlace + "&format=json&limit=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "WaselApp");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            Map[] response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map[].class
            ).getBody();

            if (response == null || response.length == 0) {
                throw new RuntimeException("Place not found: " + normalizedPlace);
            }

            String lat = (String) response[0].get("lat");
            String lon = (String) response[0].get("lon");

            double[] coordinates = new double[]{
                    Double.parseDouble(lat),
                    Double.parseDouble(lon)
            };

            coordinatesCache.put(normalizedPlace, coordinates);
            return coordinates;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch coordinates for place: " + normalizedPlace);
        }
    }

    private String normalizePlaceName(String placeName) {
        if (placeName == null) {
            return "";
        }
        return placeName.trim().toLowerCase();
    }
}