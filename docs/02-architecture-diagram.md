## Architecture Diagram

```mermaid
flowchart TD

    A[Client (Postman / Mobile App / Dashboard)] --> B[Security Filter Layer]
    B --> C[Controllers Layer]
    C --> D[Service Layer]
    D --> E[Repository Layer]
    E --> F[(PostgreSQL Database)]

    %% Security
    B --> B1[JwtAuthenticationFilter]
    B1 --> B2[JwtService]

    %% Controllers
    C --> C1[AuthenticationController]
    C --> C2[CheckpointController]
    C --> C3[IncidentController]
    C --> C4[ReportController]
    C --> C5[RouteController]
    C --> C6[AlertController]
    C --> C7[AlertSubscriptionController]
    C --> C8[VoteController]
    C --> C9[ModerationController]
    C --> C10[ModerationHistoryController]
    C --> C11[AdminController]

    %% Services
    D --> D1[CheckpointService]
    D --> D2[IncidentService]
    D --> D3[ReportService]
    D --> D4[RouteService]
    D --> D5[WeatherService]
    D --> D6[AlertService]
    D --> D7[ModerationService]
    D --> D8[VoteService]
    D --> D9[AdminService]
    D --> D10[AlertSubscriptionService]
    D --> D11[GeocodingService]

    %% Repositories
    E --> E1[UserRepository]
    E --> E2[CheckpointRepository]
    E --> E3[IncidentRepository]
    E --> E4[ReportRepository]
    E --> E5[VoteRepository]
    E --> E6[AlertRepository]
    E --> E7[ModerationActionRepository]
    E --> E8[AlertSubscriptionRepository]

    %% External APIs
    D4 --> X1[OSRM API]
    D5 --> X2[OpenWeatherMap API]
    D11 --> X3[Nominatim API]