# 🚗 Wasel Palestine - Smart Mobility Platform

## 📌 Project Overview

Wasel Palestine is an API-centric smart mobility platform designed to help Palestinians navigate daily movement challenges through real-time mobility intelligence.

The platform provides structured and reliable data about:

* Road conditions
* Checkpoints
* Traffic incidents
* Weather conditions
* Route estimation

It is built as a **backend-only RESTful API system** that can be consumed by mobile apps, dashboards, or third-party systems.

---

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