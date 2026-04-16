# 🚗 Wasel Palestine - Smart Mobility Platform

## 📌 Project Overview

Wasel Palestine is an API-centric smart mobility platform designed to help Palestinians navigate daily movement challenges through real-time mobility intelligence.

The platform provides structured and reliable data about:

- Road conditions
- Checkpoints
- Traffic incidents
- Weather conditions
- Route estimation

It is built as a **backend-only RESTful API system** that can be consumed by mobile apps, dashboards, or third-party systems.

---

## ✨ Core Features

| Feature | Description |
|--------|-------------|
| 🚧 Road Incidents | Create, update, verify, close incidents with filtering, sorting, pagination |
| 🚏 Checkpoints | Centralized registry with status history tracking |
| 🗺️ Route Estimation | Calculate estimated distance & duration using OpenStreetMap (OSRM) |
| 🌦️ Weather Data | Real-time weather data by coordinates with caching |
| 🔐 JWT Authentication | Secure login with role-based access |
| 📢 Alerts System | Users subscribe to incident notifications by area/category |

---

## 🛠️ Tech Stack

| Component | Technology |
|----------|------------|
| Backend | Spring Boot + Java 21 |
| Database | PostgreSQL |
| ORM | JPA / Hibernate |
| Authentication | JWT |
| Build Tool | Maven |
| External APIs | OSRM, OpenWeatherMap |
| Testing | k6, API-Dog |
| Deployment | Docker |

---

## 🏗️ System Architecture

```text
Client Apps / Dashboard
        ↓
 REST API Controllers
        ↓
   Service Layer
        ↓
 Repository Layer
        ↓
   PostgreSQL DB

External APIs:
- OSRM Routing API
- OpenWeather API

Full architecture details available in:

docs/02-architecture-diagram.md

🌐 API Base URL
http://localhost:8080/api/v1

🚀 Quick Start
Prerequisites
Java 21
PostgreSQL
Maven
Docker (optional)
Installation
git clone https://github.com/nebal24/ASE_WaselPalestine.git
cd ASE_WaselPalestine
./mvnw spring-boot:run

👤 Test Users
| Role      | Email                                           | Password    |
| --------- | ----------------------------------------------- | ----------- |
| ADMIN     | [admin@wasel.ps](mailto:admin@wasel.ps)         | password123 |
| MODERATOR | [moderator@wasel.ps](mailto:moderator@wasel.ps) | 123456      |
| USER      | [user@wasel.ps](mailto:user@wasel.ps)           | 123456      |

📚 Project Documentation
| Document             | Location                        |
| -------------------- | ------------------------------- |
| API Documentation    | API-Dog Collection              |
| Performance Report   | Performance_Report.md           |
| System Overview      | docs/01-system-overview.md      |
| Architecture Diagram | docs/02-architecture-diagram.md |
| Database Schema      | docs/03-database-schema.md      |
| API Design           | docs/04-api-design.md           |
| External APIs        | docs/05-external-apis.md        |
| Testing Strategy     | docs/06-testing-strategy.md     |
| Performance Results  | docs/07-performance-results.md  |

🔍 Example API Endpoints
POST   /api/v1/auth/login
GET    /api/v1/incidents
POST   /api/v1/incidents
PATCH  /api/v1/incidents/{id}/verify
GET    /api/v1/routes/estimate
GET    /api/v1/weather?lat=31.5&lon=34.4

📊 Test Results
API Testing (API-Dog)
Total Tests: 33
Passed: 33
Failed: 0
Performance Testing (k6)
| Scenario    | VUs | Avg Response | p95   | Error Rate |
| ----------- | --- | ------------ | ----- | ---------- |
| Read-Heavy  | 10  | 331ms        | 3.03s | 0%         |
| Write-Heavy | 10  | 451ms        | 4.34s | 0%         |
| Mixed       | 20  | 322ms        | 3.02s | 0%         |
| Spike       | 200 | 317ms        | 3.01s | 0%         |
| Soak        | 20  | 14ms         | 31ms  | 0%         |

🔐 Security Features
JWT Access + Refresh Tokens
Password Encryption
Role-Based Authorization
Input Validation
Protected Admin / Moderator Routes

👥 Team Members
| Name  |
|-------|
| Amaal |
| Nebal |
| Afnan |
| Sanaa |

© 2026 Wasel Palestine

Advanced Software Engineering Course Project


