package com.wasel.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate = new RestTemplate();

    public double[] getCoordinatesFromPlace(String placeName) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?q="
                    + placeName + "&format=json&limit=1";

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
                throw new RuntimeException("Place not found: " + placeName);
            }

            String lat = (String) response[0].get("lat");
            String lon = (String) response[0].get("lon");

            return new double[]{
                    Double.parseDouble(lat),
                    Double.parseDouble(lon)
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch coordinates for place: " + placeName);
        }
    }
}