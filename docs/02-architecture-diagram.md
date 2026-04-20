## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│              Client (Postman / Mobile App / Dashboard)       │
└─────────────────────────┬───────────────────────────────────┘
                          │  HTTP Requests
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                  Security Filter Layer                        │
│          JwtAuthenticationFilter  ←→  JwtService            │
│   (validates every request before it reaches controllers)    │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                    Controllers Layer                          │
│                                                              │
│  AuthController       CheckpointController                   │
│  IncidentController   ReportController                       │
│  RouteController      AlertController                        │
│  AlertSubscriptionController   VoteController                │
│  ModerationController  AdminController                       │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                     Service Layer                             │
│                                                              │
│  CheckpointService    IncidentService    ReportService        │
│  RouteService         WeatherService    AlertService         │
│  ModerationService    VoteService       AdminService         │
│  AlertSubscriptionService               GeocodingService     │
└────────────┬──────────────────────────────┬─────────────────┘
             │                              │
             ▼                              ▼
┌────────────────────────┐    ┌─────────────────────────────┐
│   Repository Layer     │    │      External APIs           │
│   (Spring Data JPA)    │    │                              │
│                        │    │  OSRM Routing API            │
│  UserRepository        │    │  (router.project-osrm.org)   │
│  CheckpointRepository  │    │  ↑ called by RouteService    │
│  IncidentRepository    │    │                              │
│  ReportRepository      │    │  OpenWeatherMap API          │
│  VoteRepository        │    │  (api.openweathermap.org)    │
│  AlertRepository       │    │  ↑ called by WeatherService  │
│  ModerationRepository  │    │  ↑ cached for 10 minutes     │
└────────────┬───────────┘    └─────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│                    PostgreSQL Database                        │
│                                                              │
│  users          checkpoints       checkpoint_status_history  │
│  incidents      reports           votes                      │
│  moderation_actions               alert_subscriptions        │
│  alerts                                                      │
└─────────────────────────────────────────────────────────────┘
```