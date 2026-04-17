Wasel	Palestine	– Smart	Mobility	&	Checkpoint	Intelligence
Platform
Wasel Palestine is an API-centric smart mobility platform designed to support Palestinians in
navigating daily movement challenges by providing structured, reliable, and up-to-date mobility
intelligence.
The platform aggregates data related to road conditions, checkpoints, traffic incidents, and
environmental factors, and exposes this information through a well-defined backend API that can
be consumed by mobile applications, web dashboards, or third-party systems.
The system focuses exclusively on backend engineering concerns, including API design, data
modeling, external data integration, performance optimization, and system reliability. User
interface development is outside the scope of this project.
Application requirements
For this project, you may choose one backend technology from the following options: Spring
Boot (Java), Node.js using Express.js or NestJS, or Django REST Framework / FastAPI
(Python). Your selected technology stack must be clearly justified in the project
documentation, with explicit reasoning related to scalability, security, maintainability, and
development efficiency.
• Relational databse is mandatory ( Raw queries and ORM).
• The backend must expose the system functionality entirely through versioned APIs
(/api/v1/...).
• RESTful APIs are required. - Optional (bonus): GraphQL for read-only endpoints.
• Docker must be used for application deployment.
• Authentication must be implemented using JWT (access + refresh tokens) or OAuth2.

Project planning and version control
A Git-based workflow is mandatory. Every project member must use feature branches, pull
requests for merging into the main branch, and meaningful commit messages. All development
activity must be traceable through version control history.
Core Features:
1. Road Incidents & Checkpoint Management
   • Centralized registry of checkpoints, road closures, delays, and hazardous conditions.
   • Each checkpoint maintains a status history to track changes over time.
   • Incidents are categorized by type (closure, delay, accident, weather hazard, etc.) and
   severity.
   • Authorized users (moderators/admins) can create, update, verify, and close incidents.
   • Full support for filtering, sorting, and pagination via API endpoints.
2. Crowdsourced Reporting System
   • Citizens can submit reports related to mobility disruptions.
   o Each report includes: (geographic location, category, description, timestamp)
   • Validation and abuse-prevention mechanisms are required.
   • Duplicate report detection and moderation workflow.
   • Community-based credibility indicators (e.g., voting or confidence scoring).
   • All moderation actions must be auditable.
3. Route Estimation & Mobility Intelligence
   • API endpoints that estimate routes between two locations.
   o Route estimation must provide: (estimated distance, estimated duration,
   explanatory metadata indicating factors affecting the route)
   o Supports constraints such as: (avoiding checkpoints, avoiding specific areas)
   • Route estimation may rely on heuristics; exact accuracy is not required.
4. Alerts & Regional Notifications
   • Users can subscribe to alerts based on: (geographic area, incident category)
   • New verified incidents trigger alert records.
   • Designed to allow future integration with external notification services.
   External API Integration:
   In addition to the main features, Integrate with external APIs that provide data from authoritative
   sources, (this enhances the platform's data accuracy and comprehensiveness).
   The system must integrate with at least two external APIs, including:
   • A routing or geolocation service (e.g., OpenStreetMap-based providers)
   • A contextual data provider such as a weather API
   Integrations must properly handle: (authentication, rate limiting, timeouts, caching)
   API Documentation & Testing (API-Dog): All APIs must be documented using API-Dog, including:
   • endpoint descriptions
   • authentication flows
   • request and response schemas
   • error formats
   Deliverables include:
   • API-Dog collection export
   • environment configurations
   • test execution results
   Performance & Load Testing (Mandatory): Evaluate system performance using k6.
   Required Test Scenarios
   • read-heavy workloads (incident listing)
   • write-heavy workloads (report submissions)
   • mixed workloads
   • spike testing
   • sustained load (soak testing)
   Metrics to Report
   • average response time
   • p95 latency
   • throughput
   • error rate
   • identified bottlenecks
   A performance report must explain:
   • observed limitations
   • root causes
   • optimizations applied
   • before/after comparison
   Documentation Requirements (Wiki or README): Documentation must include:
   • system overview
   • architecture diagram
   • database schema (ERD)
   • API design rationale
   • external API integration details
   • testing strategy
   • performance testing results
   Documentation must be clear, structured, and complete.
   Evaluation Criteria
   Area Weight
   API Design & Architecture 30%
   Version Control 10%
   Database 15%
   Correctness & Security 10%
   External API Integrations 5%
   Performance & Load Analysis 20%
   Documentation & Clarity 10%
   Final Notes
   This project simulates real-world backend system development under non-ideal conditions,
   including unreliable external services, incomplete data, and performance constraints. You are
   expected to demonstrate sound engineering judgment, justify architectural decisions, and apply
   professional software engineering practices throughout the project lifecycle.