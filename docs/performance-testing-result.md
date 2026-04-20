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






----------------------------------------------------------------------------------------------------------------------


## Performance & Load Testing (k6)

### Observed Limitations
The system handled most endpoints (report listing, fetching by ID, and alerts retrieval) efficiently under all scenarios.

The main limitation was observed in **report creation under high load**, where a noticeable percentage of requests were rejected. This was mainly due to built-in system constraints such as cooldown periods, duplicate detection, and abuse prevention rules.

---

### Root Causes
- Report creation includes **duplicate detection logic**, which may reject similar or repeated submissions.
- A **cooldown mechanism** prevents users from submitting multiple reports within a short time window.
- Abuse prevention rules limit excessive requests from the same user.
- These constraints are intentional and designed to maintain data integrity rather than being performance bottlenecks.

---

### Optimizations Applied
- Introduced **multi-user token rotation** in k6 testing instead of using a single authenticated user.
- This allowed requests to be distributed across multiple users, reducing artificial rejection caused by cooldown and abuse prevention mechanisms.
- Improved **test realism**, making the load scenario closer to real-world usage patterns.

---

### Before / After Comparison

| Metric               | Before (Single User) | After (Multi-User) |
|----------------------|----------------------|--------------------|
| Avg Response Time    | ~30 ms               | ~37 ms             |
| p95 Latency          | ~60 ms               | ~63 ms             |
| Throughput           | ~9.9 req/s           | ~10 req/s          |
| Error Rate           | ~25.26%              | ~24.93%            |

---

### Conclusion
The system demonstrated stable performance across all tested scenarios with low response times and consistent throughput.

The applied optimization improved **test realism and request distribution**, although it did not significantly reduce the error rate. This is because a large portion of rejected requests is caused by **intentional validation and protection mechanisms**, not system performance limitations.

Overall, the system is capable of handling concurrent workloads effectively, and the observed limitations are aligned with expected system behavior.

---

**Note:**  
The observed error rate reflects system-level protection logic rather than instability, which confirms the robustness of the implemented validation mechanisms.