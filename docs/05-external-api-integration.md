## 1. OSRM Routing API (OpenStreetMap)
The system integrates with the OSRM (Open Source Routing Machine) API, which is based on OpenStreetMap data, to calculate routes between two geographic points.

### Purpose
- Calculate distance between two locations
- Estimate travel duration
- Support route estimation functionality in the system

### Where it is used
- Implemented in: `RouteService`
- External communication handled by: `OpenStreetMapClient`

### Endpoint
http://router.project-osrm.org/route/v1/driving/{lng,lat};{lng,lat}

### Authentication
- No authentication is required (public API)

### Failure Handling
- If the API request fails or returns invalid data, the system falls back to an internal distance calculation method (Haversine formula)

### Design Considerations
- The routing logic is isolated inside a dedicated service
- External calls are made only when needed (on-demand)
- This reduces unnecessary external requests and improves performance

## 2. OpenWeatherMap API
The system integrates with the OpenWeatherMap API to retrieve weather information for a given geographic location.

### Purpose
- Retrieve real-time weather data by coordinates
- Support weather-aware mobility context
- Enrich route and incident-related information with environmental conditions

### Where it is used
- Implemented in: `WeatherService`

### Endpoint
`https://api.openweathermap.org/data/2.5/weather`

### Authentication
- Access is secured using an API key
- The API key is configured through application properties / environment configuration

### Caching
- Weather responses are cached per location
- This reduces repeated external calls and improves performance
- Cached entries are reused for approximately 10 minutes per location

### Timeout Handling
- External requests are protected using configured HTTP timeout settings
- This prevents the application from waiting too long for slow API responses

### Failure Handling
- If the weather API fails or becomes unavailable, the system returns a safe fallback weather response
- This ensures that the core system continues working without crashing

### Design Considerations
- Weather integration is isolated in a dedicated service
- Caching reduces unnecessary API usage
- Graceful fallback handling improves system reliability

## 3. Nominatim Geocoding API
The system integrates with the Nominatim API to convert human-readable location names into geographic coordinates.

### Purpose
- Convert place names into latitude and longitude
- Support location-based features such as routing and reporting
- Improve usability for location input

### Where it is used
- Implemented in: `GeocodingService`

### Endpoint
https://nominatim.openstreetmap.org/search

### Authentication
- No authentication is required (public API)

### Failure Handling
- If the API fails, the system handles the error gracefully without crashing
- Features depending on geocoding will not break the entire system

### Design Considerations
- Geocoding is handled in a separate service
- External dependency is isolated from core logic
- Designed to support future enhancements for location-based features