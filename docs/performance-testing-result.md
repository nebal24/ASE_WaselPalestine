## Performance & Load Testing (k6)

### Observed Limitations
Most endpoints (reports, voting, moderation, and audit history) performed consistently under all scenarios.  
The main limitation was **alert subscription creation**, which had the highest failure rate (~22%).

Report creation also showed occasional rejections due to duplicate detection and anti-spam rules, which are expected system behaviors.

---

### Root Causes
- Duplicate detection and validation logic in report creation can reject repeated or similar requests.
- Alert subscriptions involve stricter validation and geocoding, making them more sensitive to repeated or conflicting inputs.
- External geocoding calls (before optimization) added latency and increased failure risk.

---

### Optimizations Applied
- Added **in-memory caching** in `GeocodingService` to avoid repeated external API calls for the same place names.
- Improved **URL encoding** for safer geocoding requests.
- Refined k6 test inputs to better reflect realistic usage.

---

### Before / After Comparison

The following table shows the impact of the applied backend optimization:

| Metric | Before | After |
|--------|--------|-------|
| Avg Response Time | ~93.8 ms | 52.26 ms |
| p95 Latency | ~276 ms | 77.32 ms |
| Throughput | ~2.46 req/s | 2.50 req/s |
| Error Rate | ~8.43% | 6.07% |

---

### Conclusion
The system handled all required scenarios efficiently with low latency and stable throughput.  
The applied optimization significantly improved performance.

The only remaining bottleneck is **alert subscription creation**, which may require further backend refinement.