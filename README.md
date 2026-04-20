# 🚗 Wasel Palestine - Smart Mobility Platform

## System Overview

Wasel Palestine is a backend-centric smart mobility platform designed to help Palestinians navigate daily movement challenges through structured, reliable, and up-to-date mobility intelligence. The system exposes its functionality through versioned RESTful APIs that support client applications such as mobile apps, web dashboards, and third-party systems.

The platform manages mobility-related data including road incidents, checkpoints, traffic disruptions, route estimation, weather context, and regional alerts. It also supports crowdsourced reporting, moderation workflows, credibility indicators, and external API integration for routing/geolocation and contextual environmental data.

This project focuses on backend software engineering concerns, including API design, data modeling, database interaction, authentication and authorization, external service integration, performance evaluation, and system reliability. A more detailed explanation of the system scope, components, and responsibilities is provided in the full System Overview document.

**Detailed document:** `docs/system-overview.md`

## ✨ Core Features

| Feature               | Description                                                                 |
| --------------------- | --------------------------------------------------------------------------- |
| 🚧 Road Incidents     | Create, update, verify, close incidents with filtering, sorting, pagination |
| 🚏 Checkpoints        | Centralized registry with status history tracking                           |
| 🗺️ Route Estimation  | Calculate estimated distance & duration using OpenStreetMap (OSRM)          |
| 🌦️ Weather Data      | Real-time weather data by coordinates with caching                          |
| 🔐 JWT Authentication | Secure login with role-based access                                         |
| 📢 Alerts System      | Users subscribe to incident notifications by area/category                  |

---

## 🛠️ Tech Stack

| Component      | Technology            |
| -------------- | --------------------- |
| Backend        | Spring Boot + Java 21 |
| Database       | PostgreSQL            |
| ORM            | JPA / Hibernate       |
| Authentication | JWT                   |
| Build Tool     | Maven                 |
| External APIs  | OSRM, OpenWeatherMap  |
| Testing        | k6, API-Dog           |
| Deployment     | Docker                |

---

## 🏗️ Architecture Diagram

The system follows a layered architecture consisting of security, controllers, services, repositories, and a PostgreSQL database.

External integrations are isolated in dedicated services.

📄 **Full architecture diagram and explanation:**
[View Architecture Diagram](docs/02-architecture-diagram.md)

---

## 🔌 External API Integration Details

The platform integrates with external APIs to support route estimation, weather-aware mobility context, and geocoding functionality.

These integrations are handled through dedicated services with caching, timeout protection, and graceful failure handling.

📄 **Full external API integration details:**
[View External API Documentation](docs/05-external-api-integration.md)

---

## 🏗️ Architecture Diagram

The system follows a layered architecture consisting of security, controllers, services, repositories, and a PostgreSQL database.

External integrations are isolated in dedicated services.

📄 **Full architecture diagram and explanation:**  
[View Architecture Diagram](docs/02-architecture-diagram.md)

---

## 🔌 External API Integration Details

The platform integrates with external APIs to support route estimation, weather-aware mobility context, and geocoding functionality.

These integrations are handled through dedicated services with caching, timeout protection, and graceful failure handling.

📄 **Full external API integration details:**  
[View External API Documentation](docs/05-external-api-integration.md)

---

## 🌐 API Base URL

`http://localhost:8080/api/v1`

---

## 🚀 Quick Start

### Prerequisites

* Java 21
* PostgreSQL
* Maven
* Docker (optional)

### Installation

```bash
git clone https://github.com/nebal24/ASE_WaselPalestine.git
cd ASE_WaselPalestine

./mvnw spring-boot:run
```

---
## Testing Strategy

The system was tested using a combination of functional API testing and performance/load testing to ensure correctness, reliability, and scalability.

Functional testing was conducted using API-Dog, where all endpoints were validated, including authentication flows, request/response structures, and error handling scenarios.

Performance and load testing were performed using k6. Multiple scenarios were evaluated, including read-heavy workloads (incident retrieval), write-heavy workloads (report submissions), mixed workloads, spike testing, and sustained load testing. Key performance metrics such as average response time, p95 latency, throughput, and error rate were collected and analyzed.

The testing process also included identifying bottlenecks, analyzing system limitations, and applying optimizations, followed by before-and-after performance comparisons.

**Detailed report:** `docs/testing-strategy.md`
## 📊 Test Results

### API Testing (API-Dog)

* Total Tests: 33
* Passed: 33
* Failed: 0

### Performance Testing (k6)

| Scenario    | VUs | Avg Response | p95   | Error Rate |
| ----------- | --- | ------------ | ----- | ---------- |
| Read-Heavy  | 10  | 331ms        | 3.03s | 0%         |
| Write-Heavy | 10  | 451ms        | 4.34s | 0%         |
| Mixed       | 20  | 322ms        | 3.02s | 0%         |
| Spike       | 200 | 317ms        | 3.01s | 0%         |
| Soak        | 20  | 14ms         | 31ms  | 0%         |

---

## 🔐 Security Features

* JWT Access + Refresh Tokens
* Password Encryption
* Role-Based Authorization
* Input Validation
* Protected Admin / Moderator Routes

---

## 👥 Team Members

| Name  |
| ----- |
| Amaal |
| Nebal |
| Afnan |
| Sana  |

---

## © 2026 Wasel Palestine

Advanced Software Engineering Course Project

