# 🚗 Wasel Palestine - Smart Mobility Platform
![Technology Stack](/assets/WaselPalestine.png)

**Backend RESTful API Platform for Smart Mobility and Checkpoint Intelligence in Palestine**

---

## System Overview

Wasel Palestine is a backend-centric smart mobility platform designed to help Palestinians navigate daily movement challenges through structured, reliable, and up-to-date mobility intelligence.

The system exposes its functionality through versioned RESTful APIs that support client applications such as mobile apps, web dashboards, and third-party systems.

The platform manages mobility-related data including road incidents, checkpoints, traffic disruptions, route estimation, weather context, and regional alerts. It also supports crowdsourced reporting, moderation workflows, credibility indicators, and external API integration for routing/geolocation and contextual environmental data.

This project focuses on backend software engineering concerns, including API design, data modeling, database interaction, authentication and authorization, external service integration, performance evaluation, and system reliability.

A more detailed explanation of the system scope, components, and responsibilities is provided in the full System Overview document.

📄 **Detailed document:**  
[View System Overview](docs/system-overview.md)

---
## ✨ Core Features

| Feature | Description |
|---------|-------------|
| 🚧 Road Incidents & Checkpoint Management | Provides a centralized system for managing road incidents, checkpoints, closures, delays, accidents, and hazardous conditions. Authorized users can create, update, verify, and close incidents, while users can view them with filtering, sorting, and pagination. |
| 👥 Crowdsourced Reporting System | Allows citizens to submit mobility disruption reports with location, category, description, and timestamp. Reports go through validation, duplicate detection, moderation, and credibility scoring to improve reliability. |
| 🗺️ Route Estimation & Mobility Intelligence | Estimates routes between two locations and returns the expected distance, duration, and metadata explaining factors that may affect the route, such as checkpoints, incidents, or avoided areas. |
| 📢 Alerts & Regional Notifications | Enables users to subscribe to alerts based on geographic areas or incident categories. When new verified incidents are added, alert records are generated to support future notification services. |
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
## ✅ Technology Stack Justification

Spring Boot with Java 21 was selected as the backend technology because it supports building secure, scalable, and maintainable RESTful APIs.

The detailed justification explains the choice in terms of scalability, security, maintainability, and development efficiency.

📄 **Full technology stack justification:**  
[View Technology Stack Justification](docs/technology-stack-justification.md)

---

## 🏗️ Architecture Diagram

Wasel Palestine follows a clean layered architecture designed for maintainability, scalability, and separation of concerns.

The system is composed of:

- 🔐 Security Layer
- 🌐 REST Controllers
- ⚙️ Business Services
- 🗄️ Repository Layer
- 🐘 PostgreSQL Database
- 🔗 External API Integration Services

External integrations are isolated in dedicated services to improve reliability and maintainability.

📄 **Full architecture diagram and explanation:**  
[View Architecture Diagram](docs/02-architecture-diagram.md)

---

## 🗄️ Database Schema (ERD)

The database schema is designed using PostgreSQL and represents the main relationships between users, checkpoints, incidents, reports, votes, alerts, subscriptions, and route requests.

It uses primary keys, foreign keys, and unique constraints to maintain data consistency and support the main backend features.

📄 **Full ERD and database explanation:**  
[View Database Schema ERD](docs/03-database-schema-erd.md)

---

## 🔗 API Design Rationale

The Wasel Palestine backend follows RESTful API principles to provide a secure, scalable, and maintainable system.

The API uses versioned endpoints (`/api/v1/`), standard HTTP methods, JWT authentication, role-based access control, and structured request/response models.

Additional features such as filtering, sorting, pagination, and standardized error handling were implemented to improve usability, performance, and long-term maintainability.

📄 **Full API design explanation:**  
[View API Design Rationale](docs/04-api-design.md)

---

## 🔌 External API Integration Details

The platform integrates with external APIs to support route estimation, weather-aware mobility context, and geocoding functionality.

These integrations are handled through dedicated services with caching, timeout protection, and graceful failure handling.

📄 **Full external API integration details:**  
[View External API Documentation](docs/05-external-api-integration.md)

---

## 🧪 Testing Strategy

The testing strategy focuses on verifying API correctness, security, integration behavior, and system performance.

API-Dog was used to document and manually test endpoints, while k6 was used to evaluate performance under different workloads such as read-heavy, write-heavy, mixed, spike, and soak testing.

The strategy covers authentication testing, CRUD operations, validation errors, role-based access control, external API behavior, and load testing results.

📄 **Full testing strategy:**  
[View Testing Strategy](docs/testing-strategy.md)

---

## 📈 Performance Testing Results

System performance was evaluated using k6 under multiple load scenarios including read-heavy, write-heavy, mixed workloads, spike testing, and sustained load testing.

Metrics such as average response time, p95 latency, throughput, and error rate were analyzed to identify bottlenecks and measure system stability under concurrent traffic.

The results were used to improve caching behavior, database efficiency, and overall API responsiveness.

📄 **Full performance report:**  
[View Performance Testing Results](docs/07-performance-results.md)

---

## 📘 API-Dog Collection Export

📄 **Exported Collection:**  
[View API-Dog Export](docs/Wasel_Application.md)

---

## 🚀 Installation & Running

Clone the repository and run the project locally:

- git clone <repository-url>
- cd Wasel-Palestine
- docker-compose up

Or run manually using Maven:

- mvn spring-boot:run

The server will run on:

http://localhost:8081

---

## 🔐 Default Test Accounts

| Role | Email | Password |
|------|-------|----------|
| ADMIN | admin@wasel.ps | password123 |
| USER | user@wasel.ps | password123 |

---

## 👥 Team Members

- Amaal
- Afnan
- Nebal
- Sana

---

## 📝 Final Notes

This project was developed as part of the Advanced Software Engineering course.

The system was designed using modern backend engineering practices with a strong focus on scalability, maintainability, performance, and real-world software architecture.

---