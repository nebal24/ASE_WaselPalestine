# Testing Strategy

## 1. Introduction

The testing strategy for Wasel Palestine focuses on validating both the functional correctness and performance of the backend REST API. Since the system is API-centric, testing ensures that endpoints behave correctly, enforce security constraints, and remain stable under different load conditions.

The strategy combines functional API testing and performance/load testing.

---

## 2. Testing Objectives

The main objectives of testing are:

- verify correctness of all API endpoints
- validate authentication and authorization mechanisms
- ensure proper request and response structures
- test error handling and edge cases
- evaluate system performance under different workloads
- identify bottlenecks and system limitations

---

## 3. Testing Scope

Testing covers all major backend modules:

- authentication and JWT handling
- incident and checkpoint management
- report submission and moderation
- voting system
- alert subscriptions
- route estimation endpoints
- filtering, sorting, and pagination behavior

Both public and protected endpoints were tested.

---

## 4. Testing Tools

### API-Dog (Functional Testing)

API-Dog was used to test and document all API endpoints. It was used to validate endpoint behavior, authentication flow, request and response schemas, and error handling.

### k6 (Performance Testing)

k6 was used to evaluate system performance under different load conditions by simulating multiple concurrent users sending requests to the API.

---

## 5. Functional Testing Approach

Functional testing was performed by sending requests to each endpoint and verifying expected outputs.

### Endpoint Validation

Each endpoint was tested for:

- correct HTTP method usage
- correct status codes
- correct response structure
- correct business logic behavior

### Authentication Testing

Authentication tests verified that:

- valid users can log in and receive JWT tokens
- protected endpoints require valid tokens
- invalid or expired tokens are rejected
- role-based access is enforced correctly

### Error Handling

Error scenarios were tested to ensure proper responses for:

- invalid input data
- unauthorized access
- forbidden operations
- non-existent resources

---

## 6. Performance Testing Strategy

Performance testing was conducted using k6 to evaluate system behavior under load.

### Metrics Collected

The following metrics were analyzed:

- average response time
- p95 latency
- throughput (requests per second)
- error rate

---

## 7. k6 Test Scenarios

### Read-Heavy Workload

Simulates frequent data retrieval operations.

Examples:
- retrieving incidents
- retrieving reports
- filtering and pagination queries

This scenario evaluates database read performance and response time under repeated access.

---

### Write-Heavy Workload

Simulates frequent data creation and updates.

Examples:
- submitting reports
- creating incidents
- moderation actions

This scenario evaluates database write performance and system behavior during frequent updates.

---

### Mixed Workload

Combines read and write operations to simulate realistic system usage where multiple types of requests occur simultaneously.

---

### Spike Testing

Simulates a sudden increase in concurrent users within a short period of time. This evaluates system stability under unexpected traffic spikes.

---

### Soak Testing

Runs the system under sustained load for an extended period of time. This evaluates long-term stability and consistency.

---

## 8. Test Execution Workflow

The testing process followed these steps:

1. start the backend application and database
2. authenticate to obtain a JWT token
3. execute functional tests using API-Dog
4. run k6 scripts against selected endpoints
5. collect performance metrics
6. analyze system behavior

---

## 9. Performance Analysis

Performance results were analyzed to identify:

- slow endpoints
- differences between read and write performance
- impact of external API calls
- efficiency of filtering and pagination
- system stability under load

---

## 10. Bottlenecks

The main bottlenecks observed include:

- database query performance under repeated filtering and sorting
- additional latency caused by external API calls (routing and weather services)
- increased processing time during write-heavy operations

---

## 11. Optimization

Based on the observed bottlenecks, the system was improved by:

- reducing unnecessary repeated external API calls through caching
- optimizing database queries for frequently accessed endpoints
- improving request handling in service-level logic

---

## 12. Results Summary

The system demonstrated stable performance under different workloads. Read-heavy operations showed lower response times compared to write-heavy operations, while mixed workloads reflected realistic system behavior.

Spike testing showed that the system remains functional under sudden load increases, while soak testing confirmed stability over sustained execution.

---

## 13. Summary

The testing strategy combines functional validation and performance evaluation to ensure that the Wasel Palestine API is correct, secure, and reliable. Functional testing verifies endpoint behavior and authentication flows, while k6 evaluates system performance under multiple workload patterns. Together, these approaches provide a comprehensive assessment of system quality.