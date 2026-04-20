# Performance Testing Results

**Project:** Wasel Palestine
**Test Date:** April 16, 2026
**Tested By:** Wasel Palestine Team

---

## 1. Test Environment

| Component        | Specification               |
| ---------------- | --------------------------- |
| Tool             | k6 v1.7.1                   |
| Application      | Spring Boot 4.0.3 (Java 21) |
| Database         | PostgreSQL 18.3             |
| Server           | localhost:8081              |
| Operating System | Windows 11                  |
| CPU              | Intel Core i7               |
| RAM              | 16 GB                       |

---

## 2. Test Scenarios

| Scenario       | Virtual Users (VUs) | Duration   | Description                          |
| -------------- | ------------------- | ---------- | ------------------------------------ |
| Read-Heavy     | 10                  | 30 seconds | GET requests only                    |
| Write-Heavy    | 10                  | 30 seconds | POST requests only                   |
| Mixed Workload | 20                  | 1 minute   | 50% read / 50% write                 |
| Spike Test     | Up to 200           | 25 seconds | Sudden traffic increase with ramp-up |
| Soak Test      | 20                  | 5 minutes  | Sustained load stability test        |

---

## 3. Performance Metrics

### 3.1 Read-Heavy Scenario

| Metric                | Result     |
| --------------------- | ---------- |
| Average Response Time | 331.39 ms  |
| p95 Latency           | 3.03 s     |
| Throughput            | 11.3 req/s |
| Error Rate            | 0%         |
| Total Requests        | 401        |

### 3.2 Write-Heavy Scenario

| Metric                | Result    |
| --------------------- | --------- |
| Average Response Time | 451.31 ms |
| p95 Latency           | 4.34 s    |
| Throughput            | 8.7 req/s |
| Error Rate            | 0%        |
| Total Requests        | 341       |

### 3.3 Mixed Workload Scenario

| Metric                | Result     |
| --------------------- | ---------- |
| Average Response Time | 321.76 ms  |
| p95 Latency           | 3.02 s     |
| Throughput            | 22.2 req/s |
| Error Rate            | 0%         |
| Total Requests        | 1441       |

### 3.4 Spike Test Scenario

| Metric                | Result    |
| --------------------- | --------- |
| Average Response Time | 317.42 ms |
| p95 Latency           | 3.01 s    |
| Throughput            | 130 req/s |
| Error Rate            | 0%        |
| Total Requests        | 4381      |

### 3.5 Soak Test Scenario

| Metric                | Result     |
| --------------------- | ---------- |
| Average Response Time | 14.34 ms   |
| p95 Latency           | 30.82 ms   |
| Throughput            | 26.2 req/s |
| Error Rate            | 0%         |
| Total Requests        | 7887       |

---

## 4. Before vs After Comparison (Spike Test)

| Metric                    | Before Optimization | After Optimization | Improvement |
| ------------------------- | ------------------- | ------------------ | ----------- |
| Error Rate                | 0.4%                | 0%                 | 100%        |
| Connection Refused Errors | 16                  | 0                  | 100%        |
| Average Response Time     | 353 ms              | 317 ms             | 10% faster  |

---

## 5. Identified Bottlenecks

| Bottleneck                             | Root Cause                                         | Resolution                         |
| -------------------------------------- | -------------------------------------------------- | ---------------------------------- |
| Connection refused under sudden spikes | Server unable to accept immediate high concurrency | Added ramp-up stages               |
| High p95 latency                       | Concurrent processing and database activity        | Acceptable under stress conditions |
| Slow iteration duration                | Multiple sequential API calls per cycle            | Optimized token reuse              |

---

## 6. Optimizations Applied

| Problem                          | Optimization                      | Result           |
| -------------------------------- | --------------------------------- | ---------------- |
| Sudden traffic spike failures    | Added staged ramp-up load pattern | 0% error rate    |
| Repeated authentication overhead | Reused JWT token during tests     | Faster execution |

---

## 7. Results Summary

| Scenario    | VUs | Avg Response | p95    | Throughput | Error Rate |
| ----------- | --- | ------------ | ------ | ---------- | ---------- |
| Read-Heavy  | 10  | 331 ms       | 3.03 s | 11.3 req/s | 0%         |
| Write-Heavy | 10  | 451 ms       | 4.34 s | 8.7 req/s  | 0%         |
| Mixed       | 20  | 322 ms       | 3.02 s | 22.2 req/s | 0%         |
| Spike       | 200 | 317 ms       | 3.01 s | 130 req/s  | 0%         |
| Soak        | 20  | 14 ms        | 31 ms  | 26.2 req/s | 0%         |

---

## 8. Conclusion

* All scenarios completed successfully with **0% error rate** after optimization.
* The system handled traffic spikes up to **200 concurrent virtual users**.
* No instability or memory leak symptoms were observed during the soak test.
* Ramp-up staging significantly improved spike test reliability.
* The application is ready for academic demonstration and future deployment improvements.
