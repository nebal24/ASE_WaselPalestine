package com.wasel.service;

import com.wasel.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherService {

    // Injected from application.yml
    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    // Cache the result for 10 minutes — same location won't call
    // the API again within that time (handles rate limiting)
    @Cacheable(value = "weather", key = "#lat + ',' + #lon")
    public WeatherResponse getWeather(Double lat, Double lon) {
        try {
            String url = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .toUriString();

            // Timeout is handled by RestTemplate config (we'll add it next)
            Map response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                return fallbackWeather();
            }

            // Parse the response from OpenWeatherMap
            Map weather = (Map) ((java.util.List) response.get("weather")).get(0);
            Map main = (Map) response.get("main");
            Map wind = (Map) response.get("wind");

            return new WeatherResponse(
                    (String) weather.get("main"),
                    (String) weather.get("description"),
                    ((Number) main.get("temp")).doubleValue(),
                    ((Number) wind.get("speed")).doubleValue(),
                    ((Number) main.get("humidity")).intValue()
            );

        } catch (Exception e) {
            // If API fails for any reason, return a fallback
            // so the rest of the system keeps working
            return fallbackWeather();
        }
    }

    private WeatherResponse fallbackWeather() {
        return new WeatherResponse("Unknown", "Weather data unavailable", null, null, null);
    }
}