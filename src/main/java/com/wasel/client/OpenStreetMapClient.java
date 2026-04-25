package com.wasel.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenStreetMapClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String OSRM_URL = "http://router.project-osrm.org/route/v1/driving";


    public double[] getRoute(double startLng, double startLat, double endLng, double endLat) {
        try {
            DecimalFormat df = new DecimalFormat("#.########",
                    new java.text.DecimalFormatSymbols(Locale.US));

            String startLngStr = df.format(startLng);
            String startLatStr = df.format(startLat);
            String endLngStr = df.format(endLng);
            String endLatStr = df.format(endLat);

            String url = String.format("%s/%s,%s;%s,%s?overview=false",
                    OSRM_URL, startLngStr, startLatStr, endLngStr, endLatStr);

            log.info("Calling OSRM API: {}", url);
            String response = restTemplate.getForObject(url, String.class);
            log.info("OSRM Response: {}", response);

            JsonNode root = objectMapper.readTree(response);
            String code = root.path("code").asText();
            if (!"Ok".equals(code)) {
                log.warn("OSRM returned error code: {}", code);
                return null;
            }

            JsonNode route = root.path("routes").get(0);
            if (route == null || route.isMissingNode()) {
                log.warn("No routes found in response");
                return null;
            }

            double distance = route.path("distance").asDouble();
            double duration = route.path("duration").asDouble();

            log.info("OSRM parsed: distance={}m, duration={}s", distance, duration);
            return new double[]{distance, duration};

        } catch (Exception e) {
            log.error("OSRM API call failed: {}", e.getMessage());
            return null;  // ← هنا بيرجع null، وبالتالي الـ Service يستخدم fallback
        }
    }
}