# Architecture Diagram
```
┌──────────────────────────────────────────────────────────────────┐
│           Client (Postman / Mobile App / Dashboard)              │
└──────────────────────────────┬───────────────────────────────────┘
                               │  HTTP Requests
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                      Security Filter Layer                       │
│           JwtAuthenticationFilter  ←→  JwtService                │
│(validates protected requests before reaching secured controllers)│
└──────────────────────────────┬───────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                       Controllers Layer                          │
│                                                                  │
│   AuthenticationController      CheckpointController             │
│   IncidentController            ReportController                 │
│   RouteController               AlertController                  │
│   AlertSubscriptionController   VoteController                   │
│   ModerationController          ModerationHistoryController      │
│   AdminController                                                │
└──────────────────────────────┬───────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                        Service Layer                             │
│                                                                  │
│   CheckpointService     IncidentService      ReportService       │
│   RouteService          WeatherService       AlertService        │
│   ModerationService     VoteService          AdminService        │
│   AlertSubscriptionService                  GeocodingService     │
└──────────────┬────────────────────────────────┬──────────────────┘
               │                                │
               ▼                                ▼
┌──────────────────────────────┐  ┌─────────────────────────────────┐
│       Repository Layer       │  │         External APIs           │
│      (Spring Data JPA)       │  │                                 │
│                              │  │  OSRM Routing API               │
│  UserRepository              │  │  (router.project-osrm.org)      │
│  CheckpointRepository        │  │  ↑ called by RouteService       │
│  IncidentRepository          │  │                                 │
│  ReportRepository            │  │  OpenWeatherMap API             │
│  VoteRepository              │  │  (api.openweathermap.org)       │
│  AlertRepository             │  │  ↑ called by WeatherService     │
│  ModerationActionRepository  │  │  ↑ cached 10 min per location   │
│  AlertSubscriptionRepository │  │                                 │
│  CheckpointStatusHistory     │  │  Nominatim API                  │
│  Repository                  │  │  (nominatim.openstreetmap.org)  │
│                              │  │  ↑ called by GeocodingService   │
└──────────────┬───────────────┘  └─────────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────────────────────────────┐
│                      PostgreSQL Database                         │
│                                                                  │
│   users                checkpoints        checkpoint_status_     │
│   incidents            reports            history                │
│   votes                moderation_actions alert_subscriptions    │
│   alerts                                                         │
└──────────────────────────────────────────────────────────────────┘
```
<img width="1446" height="823" alt="image" src="https://github.com/user-attachments/assets/a4f7ac23-3dd5-4847-a15f-d1f3b26fac23" />

## Explanation

The system follows a layered architecture to separate responsibilities and improve maintainability.

- The client layer includes API consumers such as Postman, dashboards, or mobile applications.
- Protected requests pass through the JWT security filter before reaching secured controllers.
- The controllers expose REST endpoints and delegate business logic to the service layer.
- The service layer contains the core application logic and communicates with both repositories and external APIs.
- The repository layer uses Spring Data JPA to access the PostgreSQL database.
- External API integrations are isolated in dedicated services to keep third-party logic separate from the core backend system.
