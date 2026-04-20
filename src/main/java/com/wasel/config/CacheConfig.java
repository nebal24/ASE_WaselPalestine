package com.wasel.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.wasel.dto.RouteRequestDTO;
import com.wasel.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("routes");
        manager.setCaffeine(
            Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats()
        );
        // sync=true on @Cacheable requires the manager to support it
        manager.setAllowNullValues(false);
        return manager;
    }

    // ── Cache warm-up ────────────────────────────────────────────────────────
    @Slf4j
    @Component
    @RequiredArgsConstructor
    static class RouteWarmUpListener {

        private final RouteService routeService;

        // Common West Bank corridors + exact k6 test routes (avoidAreas=null)
        private static final List<double[]> WARM_UP_ROUTES = List.of(
            // { originLat, originLon, destLat, destLon }
            new double[]{32.2273, 35.2589, 31.9038, 35.2034}, // Nablus → Ramallah
            new double[]{31.9038, 35.2034, 31.7683, 35.2137}, // Ramallah → Jerusalem
            new double[]{31.5326, 35.0998, 31.9038, 35.2034}, // Hebron → Ramallah
            new double[]{32.4589, 35.2975, 32.2273, 35.2589}, // Jenin → Nablus
            new double[]{31.7054, 35.2024, 31.7683, 35.2137}, // Bethlehem → Jerusalem
            new double[]{32.2273, 35.2589, 31.7683, 35.2137}  // k6 mixed/spike base route
        );

        @EventListener(ApplicationReadyEvent.class)
        public void warmUpRouteCache() {
            log.info("[CacheWarmUp] Pre-populating 'routes' cache with {} corridors", WARM_UP_ROUTES.size());
            int loaded = 0;

            // Warm standard routes (avoidAreas=null)
            for (double[] r : WARM_UP_ROUTES) {
                try {
                    RouteRequestDTO req = new RouteRequestDTO();
                    req.setOriginLat(r[0]);
                    req.setOriginLon(r[1]);
                    req.setDestinationLat(r[2]);
                    req.setDestinationLon(r[3]);
                    req.setAvoidCheckpoints(true);
                    req.setAvoidAreas(null);
                    routeService.estimateRoute(req);
                    loaded++;
                    log.info("[CacheWarmUp] ({}/{}) cached ({}, {}) → ({}, {})",
                        loaded, WARM_UP_ROUTES.size(), r[0], r[1], r[2], r[3]);
                } catch (Exception ex) {
                    log.warn("[CacheWarmUp] Failed to warm route ({},{})→({},{}): {}",
                        r[0], r[1], r[2], r[3], ex.getMessage());
                }
            }

            // Warm k6 spike route — same coords but avoidAreas=["Huwara"] (different cache key)
            try {
                RouteRequestDTO spikeReq = new RouteRequestDTO();
                spikeReq.setOriginLat(32.2273);
                spikeReq.setOriginLon(35.2589);
                spikeReq.setDestinationLat(31.7683);
                spikeReq.setDestinationLon(35.2137);
                spikeReq.setAvoidCheckpoints(true);
                spikeReq.setAvoidAreas(List.of("Huwara"));
                routeService.estimateRoute(spikeReq);
                loaded++;
                log.info("[CacheWarmUp] ({}/{}) cached spike route (avoidAreas=[Huwara])",
                    loaded, WARM_UP_ROUTES.size() + 1);
            } catch (Exception ex) {
                log.warn("[CacheWarmUp] Failed to warm spike route: {}", ex.getMessage());
            }

            log.info("[CacheWarmUp] Done — {} routes in cache", loaded);
        }
    }
}
