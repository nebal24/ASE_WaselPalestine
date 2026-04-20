Client Applications (Postman / Mobile App / Dashboard)
                         ↓ HTTP Requests

                [Security Layer]
     JwtAuthenticationFilter  →  JwtService
                        ↓

             [REST Controllers Layer]
AuthenticationController | AdminController
CheckpointController | IncidentController | ReportController
RouteController | AlertController | AlertSubscriptionController
ModerationController | ModerationHistoryController | VoteController

                        ↓

                 [Service Layer]
AuthenticationService | AdminService
CheckpointService | IncidentService | ReportService
RouteService | RouteMetadataService | WeatherService | GeocodingService
AlertService | AlertSubscriptionService
ModerationService | ModerationHistoryService | ModerationAuditService
VoteService | NotificationService

                        ↓

             [Repository Layer - JPA]
UserRepository | CheckpointRepository | CheckpointStatusHistoryRepository
IncidentRepository | ReportRepository | VoteRepository
ModerationActionRepository | AlertRepository | AlertSubscriptionRepository

                        ↓

                [PostgreSQL Database]
Tables: users, checkpoints, checkpoint_status_history,
incidents, reports, votes, moderation_actions,
alerts, alert_subscriptions

External APIs:
RouteService / OpenStreetMapClient ──→ OSRM API
WeatherService ─────────────────────→ OpenWeatherMap API
GeocodingService ───────────────────→ Nominatim API