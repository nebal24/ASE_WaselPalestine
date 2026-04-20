# System Overview — Wasel Palestine

## 1. Introduction

Wasel Palestine is a backend-only RESTful API platform designed to support Palestinians in navigating daily movement challenges. It provides structured, reliable, and up-to-date mobility intelligence covering road conditions, checkpoints, traffic incidents, and environmental factors.

The system exposes its functionality through versioned REST APIs (`/api/v1/...`) that can be consumed by mobile applications, web dashboards, or third-party systems. The project focuses entirely on backend engineering, including API design, data modeling, authentication, external integration, and system reliability, with no frontend included in scope.

---

## 2. System Objectives

The platform is designed to achieve the following objectives:

- **Centralized Mobility Intelligence**  
  Aggregate and expose structured data related to checkpoints, incidents, and road conditions.

- **Community-Driven Reporting**  
  Enable users to contribute real-time reports about mobility disruptions.

- **Data Reliability and Trust**  
  Ensure information accuracy through moderation workflows and community credibility indicators.

- **Route Awareness**  
  Provide route estimation that reflects real-world mobility constraints.

- **Proactive Alerts**  
  Allow users to subscribe to location-based and category-based notifications.

- **Secure Access Control**  
  Protect sensitive operations using authentication and role-based authorization.

---

## 3. System Scope

Wasel Palestine is implemented as a backend API system only. It does not include any frontend interface. All interactions with the system are performed through HTTP requests to REST endpoints.

The system covers:

- incident and checkpoint management
- crowdsourced reporting and validation
- moderation workflows and audit logging
- voting and credibility scoring
- route estimation using external services
- weather-based contextual data
- alert subscription and notification generation
- authentication and authorization

---

## 4. Core Functional Areas

### 4.1 Incident & Checkpoint Management

The platform maintains a centralized registry of checkpoints and mobility incidents.

- Checkpoints are stored with geographic coordinates and maintain a **status history** (e.g., OPEN, DELAYED, CLOSED).
- Incidents represent mobility disruptions and are categorized by type (e.g., closure, accident, weather hazard).
- Each incident includes severity and lifecycle status (e.g., OPEN, VERIFIED, CLOSED).
- Authorized users (moderators/admins) can create, update, verify, and close incidents.
- API endpoints support **filtering, sorting, and pagination** for efficient data retrieval.

---

### 4.2 Crowdsourced Reporting

Users can submit reports describing mobility disruptions.

- Each report includes:
    - geographic location (latitude/longitude)
    - category
    - description
    - timestamp
- Reports are not immediately trusted; they go through validation and moderation.
- The system implements:
    - duplicate detection
    - abuse-prevention logic
    - structured moderation workflow

---

### 4.3 Moderation & Auditability

To ensure data reliability:

- Reports can be:
    - verified
    - rejected
    - marked as duplicate
- Only authorized roles can perform moderation actions.
- Every moderation action is recorded as a **ModerationAction** entity, ensuring:
    - traceability
    - accountability
    - auditability of decisions

---

### 4.4 Credibility Indicators (Voting System)

The system includes a voting mechanism:

- Users can **upvote or downvote** reports.
- Votes contribute to a credibility score.
- This score helps:
    - prioritize reliable reports
    - support moderation decisions

---

### 4.5 Route Estimation

The platform provides route estimation between two locations.

- Returns:
    - estimated distance
    - estimated duration
    - contextual metadata (e.g., nearby incidents/checkpoints)
- Uses an external routing service (OSRM).
- Includes fallback logic (e.g., Haversine calculation) if the external service fails.

---

### 4.6 Alerts & Notifications

The system supports alert subscriptions:

- Users subscribe based on:
    - geographic area
    - incident category
- When a relevant incident is verified:
    - alert records are automatically generated
- The system is designed to allow future integration with:
    - email notifications
    - SMS
    - push notifications

---

### 4.7 External Data Integration

The platform integrates with external APIs to enrich data:

- **Routing API (OSRM)**  
  Used for route estimation and travel metrics.

- **Weather API (OpenWeatherMap)**  
  Provides contextual environmental data affecting mobility.

Integration is handled at the service layer and includes:
- response handling
- timeout management
- basic caching to reduce repeated calls

---

## 5. System Architecture

The system follows a layered backend architecture that separates responsibilities into distinct components. This design improves maintainability, scalability, and clarity by isolating different concerns within the application.

System flow:

Client → Controller → Service → Repository → Database  
↓  
External APIs
### Layer Responsibilities

- **Controller Layer**
    - Handles incoming HTTP requests and sends responses
    - Maps API endpoints (`/api/v1/...`) to corresponding business logic
    - Validates request structure and parameters

- **Service Layer**
    - Contains the core business logic of the system
    - Coordinates workflows such as report moderation, voting, route estimation, and alert generation
    - Integrates with external APIs (routing and weather services)
    - Applies validation rules and handles system-level operations

- **Repository Layer**
    - Manages interaction with the database
    - Uses JPA/Hibernate for ORM-based data access
    - Executes queries for retrieving, updating, and persisting data

- **Entity Layer**
    - Defines the system data model
    - Represents database tables as Java classes with relationships between them

- **Security Layer**
    - Handles authentication using JWT tokens
    - Enforces role-based authorization for protected operations
    - Ensures that only authorized users can access sensitive endpoints

### External Integration

External APIs are accessed through the service layer. These include routing services for route estimation and weather services for environmental context. Integration logic includes handling timeouts, fallback mechanisms, and basic caching to improve reliability and performance.

## 6. Data Model Overview

The system is built on a relational data model. Key entities include:

- **User**
    - Represents system users with roles (`USER`, `MODERATOR`, `ADMIN`)

- **Checkpoint**
    - Represents fixed geographic mobility points

- **CheckpointStatusHistory**
    - Tracks status changes over time

- **Incident**
    - Represents a mobility disruption event

- **Report**
    - User-submitted report of a disruption

- **Vote**
    - User feedback on report credibility

- **ModerationAction**
    - Audit log of moderation decisions

- **AlertSubscription**
    - User-defined alert preferences

- **Alert**
    - Generated notification record

These entities are connected through relational mappings to support complex workflows.

---

## 7. API Structure

All endpoints in the system follow a versioned base path:

/api/v1/...

This versioning approach ensures backward compatibility and allows future updates without breaking existing clients.

### API Design Characteristics

The API follows RESTful design principles:

- **GET** — retrieve data (e.g., incidents, reports, routes)
- **POST** — create new resources (e.g., submit reports, create incidents)
- **PUT / PATCH** — update existing resources
- **DELETE** — remove resources

### Key Features

- **Consistent Request/Response Format**
    - All endpoints return structured JSON responses
    - Error responses follow a unified format using centralized exception handling

- **Authentication & Authorization**
    - Public access is allowed for selected read endpoints (e.g., incidents, checkpoints)
    - Protected endpoints require a valid JWT token
    - Role-based access control is enforced for sensitive operations

- **Filtering, Sorting, and Pagination**
    - Endpoints support query parameters such as:
        - page
        - size
        - sortBy
        - sortDirection
    - Enables efficient handling of large datasets

- **Endpoint Organization**
    - APIs are grouped by functionality, for example:
        - /api/v1/incidents
        - /api/v1/checkpoints
        - /api/v1/reports
        - /api/v1/routes
        - /api/v1/alerts
        - /api/v1/votes
        - /api/v1/moderation
        - /api/v1/auth

This structured API design ensures clarity, consistency, and ease of integration for client applications.

## 8. Security Overview

Security is implemented using JWT-based authentication and role-based access control.

### Key Features

- stateless authentication using access tokens
- role-based authorization (`USER`, `MODERATOR`, `ADMIN`)
- protected endpoints for moderation and management
- validation of incoming requests
- secure handling of user credentials

---

## 9. Summary

Wasel Palestine is a backend smart mobility platform that integrates structured data management, crowdsourced reporting, moderation workflows, external API integration, and secure access control to provide reliable mobility intelligence.

The system is designed with clear separation of concerns, making it maintainable, scalable, and suitable for real-world backend applications under constrained and dynamic environments.