---
title: Wasel_Application
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
code_clipboard: true
highlight_theme: darkula
headingLevel: 2
generator: "@tarslib/widdershins v4.0.30"

---

# Wasel_Application

API documentation for the implemented features: duplicate report detection, moderation workflow, credibility voting, moderation audit history, and regional alert subscriptions.

Base URLs:

# Authentication

- HTTP Authentication, scheme: bearer

# afnan/Alerts & Regional Notifications

## GET Get My Alerts

GET /api/v1/alerts/me

Returns a list of alerts for the currently authenticated user.
Alerts are automatically generated when a verified incident matches the user's active subscription criteria — including geographic proximity and incident category.
Each alert includes the triggering incident's location, category, and real-time weather data at the incident site fetched from OpenWeatherMap.

Access: Available to all authenticated users (USER, MODERATOR, ADMIN). Each user receives only their own alerts.
Authentication: Requires a valid JWT Bearer token in the Authorization header.

> Response Examples

> 200 Response

```json
{
  "id": 1,
  "incidentId": 1,
  "incidentCategory": "CLOSURE",
  "incidentLatitude": 32.2211,
  "incidentLongitude": 35.2544,
  "status": "PENDING",
  "createdAt": "2026-03-26T22:56:55.487902",
  "weather": {
    "condition": "Clouds",
    "description": "broken clouds",
    "temperature": 10.47,
    "windSpeed": 1.64,
    "humidity": 88
  }
}
```

> 401 Response

```json
{
  "error": "Invalid token",
  "message": "Token is malformed or invalid"
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|Inline|

### Responses Data Schema

HTTP Status Code **200**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» id|integer|true|none||Unique alert ID|
|» incidentId|integer|true|none||ID of the incident that triggered this alert|
|» incidentCategory|string|true|none||Category of the incident e.g. CLOSURE, ACCIDENT|
|» incidentLatitude|number|true|none||Latitude coordinate of the incident location|
|» incidentLongitude|number|true|none||Longitude coordinate of the incident location|
|» status|string|true|none||Alert delivery status — PENDING or SENT|
|» createdAt|string|true|none||Timestamp when the alert was created|
|» weather|object|true|none||Current weather data at the incident location|
|»» condition|string|true|none||General weather condition e.g. Rain, Clouds|
|»» description|string|true|none||Detailed weather description e.g. broken clouds|
|»» temperature|number|true|none||Temperature in Celsius|
|»» windSpeed|number|true|none||Wind speed in meters per second|
|»» humidity|integer|true|none||Humidity percentage|

HTTP Status Code **401**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» error|string|true|none||none|
|» message|string|true|none||none|

# afnan/Crowdsourced Reporting System

## POST Create Report

POST /api/v1/reports

Allows authenticated citizens to submit a mobility disruption report.
Each report includes a geographic location, category, description, and timestamp.

Validation rules enforced:
- Description: 10–300 characters
- Latitude: between 31.0 and 32.5 (Palestine bounds)
- Longitude: between 34.0 and 35.5 (Palestine bounds)
- Category: must be one of [CLOSURE, ACCIDENT, WEATHER_HAZARD, DELAY, ROAD_WORKS]

Abuse prevention rules enforced:
- Max 5 reports per hour per user
- 2-minute cooldown between submissions
- Max 3 reports from the same location within 5 minutes

If a similar report exists within ~500m in the last 15 minutes, the report is automatically marked as DUPLICATE.

Authentication: Requires a valid JWT Bearer token.

> Body Parameters

```json
{
  "description": "Severe delay on the road to Ramallah",
  "category": "DELAY",
  "latitude": 31.89,
  "longitude": 35.2
}
```

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|body|body|object| yes |none|
|» description|body|string| yes |Report description, 10–300 characters|
|» category|body|string| yes |One of: CLOSURE, ACCIDENT, WEATHER_HAZARD, DELAY, ROAD_WORKS|
|» latitude|body|number| yes |Latitude between 31.0 and 32.5 (Palestine bounds)|
|» longitude|body|number| yes |Longitude between 34.0 and 35.5 (Palestine bounds)|
|» relatedCheckpointId|body|integer| no |Optional — ID of a related checkpoint|

> Response Examples

```json
{
  "message": "Report submitted successfully and is now in Unverified Reports",
  "reportId": 1,
  "status": "PENDING",
  "timestamp": "2026-04-01T18:34:46.782706"
}
```

```json
{
  "message": "Report submitted but marked as duplicate of report #8",
  "reportId": 9,
  "status": "DUPLICATE",
  "timestamp": "2026-04-01T19:13:05.959828"
}
```

```json
{
  "errors": [
    "Description must be at least 10 characters",
    "Latitude must be between 31.0 and 32.5 for Palestine",
    "Longitude must be between 34.0 and 35.5 for Palestine",
    "Category must be one of [ACCIDENT, DELAY, WEATHER_HAZARD, CLOSURE]"
  ]
}
```

```json
{
  "errors": [
    "You must wait 2 minutes before submitting another report"
  ]
}
```

```json
{
  "errors": [
    "You have reached the maximum of 5 reports per hour"
  ]
}
```

```json
{
  "errors": [
    "You have submitted too many reports from the same location"
  ]
}
```

```json
{
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

```json
{
  "error": "Invalid token",
  "message": "Token is malformed or invalid"
}
```

> 404 Response

```json
{
  "timestamp": "2026-04-01T19:14:27.385946100",
  "error": "NOT_FOUND",
  "message": "Checkpoint with ID 5 not found"
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|

### Responses Data Schema

HTTP Status Code **201**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» reportId|integer|true|none||Unique ID of the created report|
|» status|string|true|none||Report status — PENDING, VERIFIED, REJECTED, or DUPLICATE|
|» timestamp|string|true|none||Date and time when the report was submitted|
|» message|string|true|none||Human-readable result message|

HTTP Status Code **400**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» errors|[string]|true|none||List of validation error messages|

HTTP Status Code **401**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» error|string|true|none||none|
|» message|string|true|none||none|

HTTP Status Code **404**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» timestamp|string|true|none||none|
|» error|string|true|none||none|
|» message|string|true|none||none|

## GET Get All Reports

GET /api/v1/reports

Returns a paginated list of reports.

Supports optional filtering by:
- report status
- incident category

Query parameters:
- status: optional
- category: optional
- page: optional, default is 0
- size: optional, default is 10

If no filters are provided, the endpoint returns all reports using default pagination.

Access: Public endpoint.
Authentication: No JWT token required.
This endpoint is public to allow users to explore reports without requiring authentication.

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|status|query|string| no |Filter reports by status. Allowed values: PENDING, VERIFIED, REJECTED, DUPLICATE|
|category|query|string| no |Filter reports by category. Allowed values: CLOSURE, ACCIDENT, WEATHER_HAZARD, DELAY, ROAD_WORKS|
|page|query|integer| no |Page number starting from 0|
|size|query|integer| no |Number of reports per page|

> Response Examples

> 200 Response

```json
{
  "reports": [
    {
      "reportId": 16,
      "category": "DELAY",
      "description": "Severe delay on the road to Ramallah",
      "latitude": 31.89,
      "longitude": 35.2,
      "status": "PENDING",
      "timestamp": "2026-04-01T20:18:53.06614"
    },
    {
      "reportId": 15,
      "category": "ACCIDENT",
      "description": "Accident reported on road 60 near Bethlehem",
      "latitude": 31.7,
      "longitude": 35.19,
      "status": "DUPLICATE",
      "timestamp": "2026-04-01T20:03:37.700387"
    }
  ],
  "currentPage": 0,
  "totalPages": 2,
  "totalReports": 14,
  "pageSize": 10
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

HTTP Status Code **200**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» reports|[object]|true|none||List of reports returned for the current page|
|»» reportId|integer|true|none||Unique report ID|
|»» category|string|true|none||Report category such as DELAY, ACCIDENT, ROAD_WORKS, or CLOSURE|
|»» description|string|true|none||User-submitted report description|
|»» latitude|number|true|none||Latitude of the reported location|
|»» longitude|number|true|none||Longitude of the reported location|
|»» status|string|true|none||Current report status such as PENDING or DUPLICATE|
|»» timestamp|string|true|none||Timestamp when the report was submitted|
|» currentPage|integer|true|none||Current page number|
|» totalPages|integer|true|none||Total number of available pages|
|» totalReports|integer|true|none||Total number of matching reports|
|» pageSize|integer|true|none||Number of reports returned per page|

## GET Get Report By ID

GET /api/v1/reports/{reportId}

Returns a single report by its ID.

This endpoint retrieves the details of one specific report using the report ID provided in the path.

Path parameter:
- reportId: unique ID of the report

Access: Public endpoint.
Authentication: No JWT token required.

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|reportId|path|string| yes |none|

> Response Examples

> 200 Response

```json
{
  "reportId": 1,
  "category": "DELAY",
  "description": "Severe delay on the road to Ramallah",
  "latitude": 31.89,
  "longitude": 35.2,
  "status": "PENDING",
  "timestamp": "2026-04-01T18:34:46.782706"
}
```

> 404 Response

```json
{
  "message": "Report not found with id: 100",
  "error": "NOT_FOUND",
  "timestamp": "2026-04-17T22:43:40.839482900"
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Successfully returned the requested report.|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Report not found.|Inline|

### Responses Data Schema

HTTP Status Code **200**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» reportId|integer|true|none||Unique report ID|
|» category|string|true|none||Report category (DELAY, ACCIDENT, etc.)|
|» description|string|true|none||Report description|
|» latitude|number|true|none||Latitude of the report location|
|» longitude|number|true|none||Longitude of the report location|
|» status|string|true|none||Timestamp of report creation|
|» timestamp|string|true|none||Timestamp of report creation|

HTTP Status Code **404**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» timestamp|string|true|none||Error message indicating that the report was not found|
|» error|string|true|none||Error type|
|» message|string|true|none||Timestamp when the error occurred|

# amaal/Authentication

## POST Register the same email

POST /api/v1/auth/register

## Register the same email

Tests duplicate email validation by attempting to register an already-registered email address.

---

### 1. Endpoint

| Method | URL |
|--------|-----|
| `POST` | `/api/v1/auth/register` |

---

### 2. Description

This request intentionally attempts to register a user with an email address that already exists in the system. It is used to verify that the API correctly rejects duplicate registrations with a 409 Conflict response.

---

### 3. Headers

| Key | Value | Required |
|-----|-------|----------|
| `Content-Type` | `application/json` | Yes |

---

### 4. Authentication

No authentication required. This is a public endpoint.

---

### 5. Request Body

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | `string` | Yes | The user's full name |
| `email` | `string` | Yes | An already-registered email address |
| `password` | `string` | Yes | The user's password |
| `role` | `string` | Yes | Must be one of: USER, MODERATOR, ADMIN |

---

### 6. Example Request

```json
{
  "name": "Test User",
  "email": "user@wasel.ps",
  "password": "123456",
  "role": "USER"
}
```

---

### 7\. Success Response

N/A — this request is expected to fail. No successful response is returned.

---

### 8\. Error Responses

| Status Code | Error | Description |
| --- | --- | --- |
| `409` | Conflict | Email is already registered in the system |

**`409 Conflict`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Email already registered",
  "path": "/api/v1/auth/register"
}
```

---

### 9\. Notes

This request is intentionally designed to test duplicate email validation

The expected response is 409 Conflict

The response message should indicate that the email is already registered

> Body Parameters

```json
{
  "name": "Test User",
  "email": "User182321@wasel.ps",
  "password": "123456",
  "role": "USER"
}
```

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|Content-Type|header|string| yes |none|
|body|body|object| no |none|
|» name|body|string| yes |none|
|» email|body|string| yes |none|
|» password|body|string| yes |none|
|» role|body|string| yes |none|

> Response Examples

> 200 Response

```json
{}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

# amaal/Incidents CRUD

## POST Adding a new incident from a user

POST /api/v1/incidents

## Create Incident

Creates a new incident report submitted by an authenticated user.

---

### 1. Endpoint

| Method | URL |
|--------|-----|
| `POST` | `/api/v1/incidents` |

---

### 2. Description

This endpoint allows any authenticated user to submit a new incident report. The incident is automatically assigned an OPEN status upon creation. The returned incident ID should be saved for use in subsequent requests.

---

### 3. Headers

| Key | Value | Required |
|-----|-------|----------|
| `Content-Type` | `application/json` | Yes |
| `Authorization` | `Bearer {{user_token}}` | Yes |

---

### 4. Authentication

Requires a valid JWT token. All authenticated roles can submit incidents.

---

### 5. Request Body

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `description` | `string` | Yes | Detailed description of the incident |
| `category` | `string` | Yes | ACCIDENT, CLOSURE, WEATHER, DELAY |
| `severity` | `string` | Yes | LOW, MEDIUM, HIGH |
| `latitude` | `number` | Yes | Latitude coordinate of the incident |
| `longitude` | `number` | Yes | Longitude coordinate of the incident |
| `checkpointId` | `number` | No | Optional checkpoint ID to associate with the incident |

---

### 6. Example Request

```json
{
  "description": "Two vehicles collided at the intersection",
  "category": "ACCIDENT",
  "severity": "HIGH",
  "latitude": 31.9038,
  "longitude": 35.2034,
  "checkpointId": 1
}
```

---

### 7\. Success Response

**`201 Created`**

```json
{
  "id": 1,
  "title": "Car accident on main road",
  "description": "Two vehicles collided at the intersection",
  "category": "ACCIDENT",
  "severity": "HIGH",
  "status": "OPEN",
  "latitude": 31.9038,
  "longitude": 35.2034,
  "address": "Main Street, Ramallah",
  "createdAt": "2024-01-01T10:00:00.000+00:00",
  "updatedAt": "2024-01-01T10:00:00.000+00:00"
}
```

---

### 8\. Error Responses

| Status Code | Error | Description |
| --- | --- | --- |
| `400` | Bad Request | Missing or invalid required fields |
| `401` | Unauthorized | No token or invalid token provided |

**`400 Bad Request`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "title: must not be blank; category: must not be null",
  "path": "/api/v1/incidents"
}
```

**`401 Unauthorized`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/v1/incidents"
}
```

---

### 9\. Notes

New incidents are automatically assigned status OPEN

Save the returned id for subsequent requests (verify, close, delete)

checkpointId is optional but recommended for incidents at known checkpoints

> Body Parameters

```json
{
  "description": "Closure incident test",
  "category": "ACCIDENT",
  "severity": "MEDIUM",
  "latitude": 32.2,
  "longitude": 35.3,
  "checkpointId": 1
}
```

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|Content-Type|header|string| yes |none|
|body|body|object| no |none|
|» description|body|string| yes |none|
|» category|body|string| yes |none|
|» severity|body|string| yes |none|
|» latitude|body|number| yes |none|
|» longitude|body|number| yes |none|
|» checkpointId|body|integer| yes |none|

> Response Examples

> 200 Response

```json
{}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|none|Inline|

### Responses Data Schema

## GET Get information about the incident we created

GET /api/v1/incidents/incident_id

## Get Incident By ID

Retrieves detailed information about a specific incident by its ID.

---

### 1. Endpoint

| Method | URL |
|--------|-----|
| `GET` | `/api/v1/incidents/{id}` |

---

### 2. Description

This endpoint returns the full details of a single incident identified by its ID. All authenticated roles are permitted to access this endpoint.

---

### 3. Headers

| Key | Value | Required |
|-----|-------|----------|
| `Authorization` | `Bearer {{token}}` | Yes |

---

### 4. Authentication

Requires a valid JWT token. All roles (USER, MODERATOR, ADMIN) are allowed.

---

### 5. Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | integer | Yes | The ID of the incident to retrieve |

---

### 6. Example Request

```
GET http://localhost:8081/api/v1/incidents/1
```

---

### 7\. Success Response

**`200 OK`**

```json
{
  "id": 1,
  "title": "Car accident on main road",
  "description": "Two vehicles collided at the intersection",
  "category": "ACCIDENT",
  "severity": "HIGH",
  "status": "OPEN",
  "latitude": 31.9038,
  "longitude": 35.2034,
  "address": "Main Street, Ramallah",
  "createdAt": "2024-01-01T10:00:00.000+00:00",
  "updatedAt": "2024-01-01T10:00:00.000+00:00"
}
```

---

### 8\. Error Responses

| Status Code | Error | Description |
| --- | --- | --- |
| `401` | Unauthorized | No token or invalid token provided |
| `404` | Not Found | No incident found with the given ID |

**`401 Unauthorized`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/v1/incidents/1"
}
```

**`404 Not Found`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Incident not found with id: 1",
  "path": "/api/v1/incidents/1"
}
```

---

### 9\. Notes

Replace {id} with the actual incident ID returned from the create incident request

Use the same token that was used for authentication

> Response Examples

> 200 Response

```json
{}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

# amaal/Verify & Close (Role-Based)

## PATCH PATCH Close via moderator/admin Copy

PATCH /api/v1/incidents/incident_id

## Close Incident

Closes an incident, changing its status from VERIFIED to CLOSED.

---

### 1. Endpoint

| Method | URL |
|--------|-----|
| `PATCH` | `/api/v1/incidents/{id}/close` |

---

### 2. Description

This endpoint allows a MODERATOR or ADMIN to close an incident. Closing an incident changes its status from VERIFIED to CLOSED, indicating that the incident has been resolved and no further action is required.

---

### 3. Headers

| Key | Value | Required |
|-----|-------|----------|
| `Authorization` | `Bearer {{moderator_token}}` | Yes |

---

### 4. Authentication

Requires a valid JWT token. Only MODERATOR or ADMIN roles are allowed.

---

### 5. Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | integer | Yes | The ID of the incident to close |

---

### 6. Example Request

```
PATCH http://localhost:8081/api/v1/incidents/1/close
```

---

### 7\. Success Response

**`200 OK`**

```json
{
  "id": 1,
  "title": "Car accident on main road",
  "description": "Two vehicles collided at the intersection",
  "category": "ACCIDENT",
  "severity": "HIGH",
  "status": "CLOSED",
  "latitude": 31.9038,
  "longitude": 35.2034,
  "address": "Main Street, Ramallah",
  "createdAt": "2024-01-01T10:00:00.000+00:00",
  "updatedAt": "2024-01-01T12:00:00.000+00:00"
}
```

---

### 8\. Error Responses

| Status Code | Error | Description |
| --- | --- | --- |
| `401` | Unauthorized | No token or invalid token provided |
| `403` | Forbidden | Insufficient role permissions |
| `404` | Not Found | No incident found with the given ID |

**`401 Unauthorized`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/v1/incidents/1/close"
}
```

**`403 Forbidden`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/v1/incidents/1/close"
}
```

**`404 Not Found`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Incident not found with id: 1",
  "path": "/api/v1/incidents/1/close"
}
```

---

### 9\. Notes

Closing changes the incident status from VERIFIED to CLOSED

The incident must be VERIFIED before it can be CLOSED

USER role cannot perform this action (returns 403)

> Response Examples

> 200 Response

```json
{}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

## PATCH PATCH Verify via moderator/admin

PATCH /api/v1/incidents/incident_id/verify

## Verify Incident

Verifies an incident, changing its status from OPEN to VERIFIED.

---

### 1. Endpoint

| Method | URL |
|--------|-----|
| `PATCH` | `/api/v1/incidents/{id}/verify` |

---

### 2. Description

This endpoint allows a MODERATOR or ADMIN to verify an incident. Verifying an incident changes its status from OPEN to VERIFIED, indicating that the incident has been reviewed and confirmed by an authorized user.

---

### 3. Headers

| Key | Value | Required |
|-----|-------|----------|
| `Authorization` | `Bearer {{moderator_token}}` | Yes |

---

### 4. Authentication

Requires a valid JWT token. Only MODERATOR or ADMIN roles are allowed.

---

### 5. Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | integer | Yes | The ID of the incident to verify |

---

### 6. Example Request

```
PATCH http://localhost:8081/api/v1/incidents/1/verify
```

---

### 7\. Success Response

**`200 OK`**

```json
{
  "id": 1,
  "title": "Car accident on main road",
  "description": "Two vehicles collided at the intersection",
  "category": "ACCIDENT",
  "severity": "HIGH",
  "status": "VERIFIED",
  "latitude": 31.9038,
  "longitude": 35.2034,
  "address": "Main Street, Ramallah",
  "createdAt": "2024-01-01T10:00:00.000+00:00",
  "updatedAt": "2024-01-01T11:00:00.000+00:00"
}
```

---

### 8\. Error Responses

| Status Code | Error | Description |
| --- | --- | --- |
| `401` | Unauthorized | No token or invalid token provided |
| `403` | Forbidden | Insufficient role permissions |
| `404` | Not Found | No incident found with the given ID |

**`401 Unauthorized`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/v1/incidents/1/verify"
}
```

**`403 Forbidden`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/v1/incidents/1/verify"
}
```

**`404 Not Found`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Incident not found with id: 1",
  "path": "/api/v1/incidents/1/verify"
}
```

---

### 9\. Notes

Verifying changes the incident status from OPEN to VERIFIED

The verifiedAt timestamp is automatically set

The verifiedBy field stores the moderator/admin who performed the action

USER role cannot perform this action (returns 403)

> Response Examples

> 200 Response

```json
{}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

## PATCH PATCH Close via user

PATCH /api/v1/incidents/incident_id/close

## Close Incident (User - Unauthorized)

Tests role-based access control by attempting to close an incident using a USER token.

---

### 1. Endpoint

| Method | URL |
|--------|-----|
| `PATCH` | `/api/v1/incidents/{id}/close` |

---

### 2. Description

This request intentionally attempts to close an incident using a USER role token. Since only MODERATOR and ADMIN roles are permitted to close incidents, this request is expected to return a 403 Forbidden response. It is used to validate role-based access control enforcement.

---

### 3. Headers

| Key | Value | Required |
|-----|-------|----------|
| `Authorization` | `Bearer {{user_token}}` | Yes |

---

### 4. Authentication

Requires a valid JWT token. USER role is NOT allowed to close incidents.

---

### 5. Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | integer | Yes | The ID of the incident to close |

---

### 6. Example Request

```
PATCH http://localhost:8081/api/v1/incidents/1/close
```

---

### 7\. Success Response

N/A — this request is expected to fail with 403 Forbidden. No successful response is returned.

---

### 8\. Error Responses

| Status Code | Error | Description |
| --- | --- | --- |
| `401` | Unauthorized | No token or invalid token provided |
| `403` | Forbidden | Insufficient role permissions |

**`401 Unauthorized`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/v1/incidents/1/close"
}
```

**`403 Forbidden`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/v1/incidents/1/close"
}
```

---

### 9\. Notes
This request is intentionally designed to test role-based access control

The expected response is 403 Forbidden

Only MODERATOR and ADMIN roles can close incidents

> Response Examples

> 200 Response

```json
{}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

# amaal/Delete (Role-Based)

## DELETE DELETE  via user

DELETE /api/v1/incidents/incident_id

## Delete Incident (User - Unauthorized)

Tests role-based access control by attempting to delete an incident using a USER token.

---

### 1. Endpoint

| Method | URL |
|--------|-----|
| `DELETE` | `/api/v1/incidents/{id}` |

---

### 2. Description

This request intentionally attempts to delete an incident using a USER role token. Since only MODERATOR and ADMIN roles are permitted to delete incidents, this request is expected to return a 403 Forbidden response. It is used to validate role-based access control enforcement.

---

### 3. Headers

| Key | Value | Required |
|-----|-------|----------|
| `Authorization` | `Bearer {{user_token}}` | Yes |

---

### 4. Authentication

Requires a valid JWT token. USER role is NOT allowed to delete incidents.

---

### 5. Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | integer | Yes | The ID of the incident to delete |

---

### 6. Example Request

```
DELETE http://localhost:8081/api/v1/incidents/1
```

---

### 7\. Success Response

N/A — this request is expected to fail with 403 Forbidden. No successful response is returned.

---

### 8\. Error Responses

| Status Code | Error | Description |
| --- | --- | --- |
| `401` | Unauthorized | No token or invalid token provided |
| `403` | Forbidden | Insufficient role permissions |

**`401 Unauthorized`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/v1/incidents/1"
}
```

**`403 Forbidden`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/v1/incidents/1"
}
```

---

### 9\. Notes

This request is intentionally designed to test role-based access control

The expected response is 403 Forbidden

Only MODERATOR and ADMIN roles can delete incidents

USER role cannot perform this action

> Response Examples

> 200 Response

```json
{}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

# amaal/Sorting

## GET Ascending order: oldest first

GET /api/v1/incidents

## Sort by Created Date - Ascending

Retrieves incidents sorted by creation date in ascending order (oldest first).

---

### 1. Endpoint

| Method | URL |
|--------|-----|
| `GET` | `/api/v1/incidents?sortBy=createdAt&sortDirection=ASC` |

---

### 2. Description

This endpoint returns a paginated list of incidents sorted by the `createdAt` field in ascending order, showing the oldest reported incidents first. Additional filters can be combined with this sorting.

---

### 3. Headers

| Key | Value | Required |
|-----|-------|----------|
| `Authorization` | `Bearer {{token}}` | Yes |

---

### 4. Authentication

Requires a valid JWT token. All roles (USER, MODERATOR, ADMIN) are allowed.

---

### 5. Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `sortBy` | string | Yes | Set to `createdAt` to sort by creation date |
| `sortDirection` | string | Yes | Set to `ASC` for oldest first |
| `page` | integer | No | Page number (default: 0) |
| `size` | integer | No | Page size (default: 10) |
| `category` | string | No | Filter by category: ACCIDENT, CLOSURE, WEATHER, DELAY |
| `severity` | string | No | Filter by severity: LOW, MEDIUM, HIGH |
| `status` | string | No | Filter by status: OPEN, VERIFIED, CLOSED |

---

### 6. Example Request

```
GET http://localhost:8081/api/v1/incidents?sortBy=createdAt&sortDirection=ASC
```

---

### 7\. Success Response

**`200 OK`**

```json
{
  "content": [
    {
      "id": 1,
      "title": "First reported incident",
      "category": "CLOSURE",
      "severity": "LOW",
      "status": "CLOSED",
      "address": "Old City, Jerusalem",
      "createdAt": "2024-01-01T06:00:00.000+00:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 5,
  "size": 10,
  "number": 0
}
```

---

### 8\. Error Responses

| Status Code | Error | Description |
| --- | --- | --- |
| `401` | Unauthorized | No token or invalid token provided |

**`401 Unauthorized`**
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/v1/incidents"
}
```

---

### 9\. Notes

sortBy can be any incident field (e.g., createdAt, severity)

sortDirection must be ASC (ascending) or DESC (descending)

Can be combined with filters like category, severity, status, page, and size

Use {{token}} from login response in Authorization header

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|sortBy|query|string| yes |none|
|sortDirection|query|string| yes |none|

> Response Examples

> 200 Response

```json
{}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

# nebal/Alert Subscriptions

## POST Create alert subscription

POST /api/v1/alert-subscriptions

Protected endpoint. Creates an alert subscription for the authenticated user based on place name, radius, and incident category.

Authentication:
This endpoint requires a Bearer JWT access token in the Authorization header.

Authorization: Bearer <access_token>

> Body Parameters

```json
{
  "placeName": "Nablus",
  "radiusKm": 5,
  "category": "ACCIDENT",
  "active": true
}
```

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|body|body|[AlertSubscriptionRequest](#schemaalertsubscriptionrequest)| yes |none|

> Response Examples

> 201 Response

```json
{
  "id": 1,
  "userId": 1,
  "placeName": "Nablus",
  "centerLatitude": 32.2205316,
  "centerLongitude": 35.2569374,
  "radiusKm": 5,
  "category": "ACCIDENT",
  "active": true,
  "createdAt": "2026-04-18T19:12:48.0373118",
  "updatedAt": null
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|Subscription created successfully|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|None|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|none|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|None|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|None|

### Responses Data Schema

HTTP Status Code **201**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» id|integer(int64)|true|none||none|
|» userId|integer(int64)|true|none||none|
|» placeName|string|true|none||none|
|» centerLatitude|number(double)|true|none||none|
|» centerLongitude|number(double)|true|none||none|
|» radiusKm|number(double)|true|none||none|
|» category|[IncidentCategory](#schemaincidentcategory)|true|none||none|
|» active|boolean|true|none||none|
|» createdAt|string(date-time)|true|none||none|
|» updatedAt|string(date-time)¦null|false|none||none|

#### Enum

|Name|Value|
|---|---|
|category|CLOSURE|
|category|ACCIDENT|
|category|WEATHER_HAZARD|
|category|DELAY|
|category|ROAD_WORKS|
|category|OTHER|

## GET Get my subscriptions

GET /api/v1/alert-subscriptions/me

Protected endpoint. Returns all alert subscriptions belonging to the authenticated user.

Authentication:
This endpoint requires a Bearer JWT access token in the Authorization header.

Authorization: Bearer <access_token>

> Response Examples

> 200 Response

```json
[
  {
    "id": 1,
    "userId": 1,
    "placeName": "Nablus",
    "centerLatitude": 32.2205316,
    "centerLongitude": 35.2569374,
    "radiusKm": 5,
    "category": "ACCIDENT",
    "active": true,
    "createdAt": "2026-04-18T19:12:48.037312",
    "updatedAt": null
  }
]
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Subscriptions returned successfully|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|None|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|none|None|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|None|

### Responses Data Schema

HTTP Status Code **200**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» id|integer(int64)|true|none||none|
|» userId|integer(int64)|true|none||none|
|» placeName|string|true|none||none|
|» centerLatitude|number(double)|true|none||none|
|» centerLongitude|number(double)|true|none||none|
|» radiusKm|number(double)|true|none||none|
|» category|[IncidentCategory](#schemaincidentcategory)|true|none||none|
|» active|boolean|true|none||none|
|» createdAt|string(date-time)|true|none||none|
|» updatedAt|string(date-time)¦null|false|none||none|

#### Enum

|Name|Value|
|---|---|
|category|CLOSURE|
|category|ACCIDENT|
|category|WEATHER_HAZARD|
|category|DELAY|
|category|ROAD_WORKS|
|category|OTHER|

## PUT Update alert subscription

PUT /api/v1/alert-subscriptions/{subscriptionId}

Protected endpoint. Updates an existing alert subscription belonging to the authenticated user.

Authentication:
This endpoint requires a Bearer JWT access token in the Authorization header.

Authorization: Bearer <access_token>

> Body Parameters

```json
{
  "placeName": "Ramallah",
  "radiusKm": 8,
  "category": "DELAY",
  "active": false
}
```

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|subscriptionId|path|integer| yes |Target subscription ID.|
|body|body|[AlertSubscriptionRequest](#schemaalertsubscriptionrequest)| yes |none|

> Response Examples

> 200 Response

```json
{
  "id": 1,
  "userId": 1,
  "placeName": "Ramallah",
  "centerLatitude": 31.8978012,
  "centerLongitude": 35.1924223,
  "radiusKm": 8,
  "category": "DELAY",
  "active": false,
  "createdAt": "2026-04-18T19:12:48.037312",
  "updatedAt": null
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Subscription updated successfully|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|None|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|none|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|None|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|None|

### Responses Data Schema

HTTP Status Code **200**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» id|integer(int64)|true|none||none|
|» userId|integer(int64)|true|none||none|
|» placeName|string|true|none||none|
|» centerLatitude|number(double)|true|none||none|
|» centerLongitude|number(double)|true|none||none|
|» radiusKm|number(double)|true|none||none|
|» category|[IncidentCategory](#schemaincidentcategory)|true|none||none|
|» active|boolean|true|none||none|
|» createdAt|string(date-time)|true|none||none|
|» updatedAt|string(date-time)¦null|false|none||none|

#### Enum

|Name|Value|
|---|---|
|category|CLOSURE|
|category|ACCIDENT|
|category|WEATHER_HAZARD|
|category|DELAY|
|category|ROAD_WORKS|
|category|OTHER|

## DELETE Delete alert subscription

DELETE /api/v1/alert-subscriptions/{subscriptionId}

Protected endpoint. Deletes an existing alert subscription belonging to the authenticated user.

Authentication:
This endpoint requires a Bearer JWT access token in the Authorization header.

Authorization: Bearer <access_token>

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|subscriptionId|path|integer| yes |Target subscription ID.|

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|204|[No Content](https://tools.ietf.org/html/rfc7231#section-6.3.5)|Subscription deleted successfully (no content)|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|None|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|none|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|None|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|None|

# nebal/Voting

## POST Cast or update a vote

POST /api/v1/reports/{reportId}/votes

Protected endpoint. Allows an authenticated user to cast or update a vote on a report.

Rules:
- One vote per user per report
- Repeating the same vote has no effect
- Changing the vote updates the credibility score

This endpoint supports the community-based credibility indicator feature.

This endpoint requires authentication using a Bearer JWT token in the Authorization header:

Authorization: Bearer <access_token>

> Body Parameters

```json
{
  "voteType": "UPVOTE"
}
```

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|reportId|path|integer| yes |Target report ID.|
|body|body|object| yes |none|
|» voteType|body|[VoteType](#schemavotetype)| yes |none|

#### Enum

|Name|Value|
|---|---|
|» voteType|UPVOTE|
|» voteType|DOWNVOTE|

> Response Examples

> Vote processed successfully

```json
{
  "reportId": 1,
  "userId": 1,
  "userVote": "UPVOTE",
  "upvotes": 1,
  "downvotes": 0,
  "score": 1,
  "reportStatus": "PENDING",
  "message": "Vote added successfully."
}
```

```json
{
  "reportId": 1,
  "userId": 1,
  "userVote": "DOWNVOTE",
  "upvotes": 0,
  "downvotes": 1,
  "score": -1,
  "reportStatus": "PENDING",
  "message": "Vote updated successfully."
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Vote processed successfully|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|None|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|None|

### Responses Data Schema

HTTP Status Code **200**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» reportId|integer(int64)|true|none||none|
|» userId|integer(int64)|true|none||none|
|» userVote|[VoteType](#schemavotetype)|true|none||none|
|» upvotes|integer(int64)|true|none||none|
|» downvotes|integer(int64)|true|none||none|
|» score|integer(int64)|true|none||none|
|» reportStatus|[ReportStatus](#schemareportstatus)|true|none||none|
|» message|string|true|none||none|

#### Enum

|Name|Value|
|---|---|
|userVote|UPVOTE|
|userVote|DOWNVOTE|
|reportStatus|PENDING|
|reportStatus|VERIFIED|
|reportStatus|REJECTED|
|reportStatus|DUPLICATE|

## DELETE Remove current user's vote

DELETE /api/v1/reports/{reportId}/votes

Protected endpoint. Removes the authenticated user's vote from the target report and recalculates the report credibility score.

Authentication:
This endpoint requires a Bearer JWT access token in the Authorization header.

Authorization: Bearer <access_token>

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|reportId|path|integer| yes |Target report ID.|

> Response Examples

> Removes the current user's vote and updates report score.

```json
{
  "reportId": 1,
  "userId": 1,
  "userVote": null,
  "upvotes": 0,
  "downvotes": 0,
  "score": 0,
  "reportStatus": "PENDING",
  "message": "Vote removed successfully."
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Removes the current user's vote and updates report score.|[VoteResponse](#schemavoteresponse)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|None|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|None|

# nebal/Moderation

## GET Get moderation history

GET /api/v1/reports/{reportId}/moderation-history

Returns the moderation and related audit history for the target report. This endpoint is used to prove that moderation actions and credibility-related changes are auditable.

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|reportId|path|integer| yes |Target report ID.|

> Response Examples

> Moderation history returned

```json
[
  {
    "actionId": 2,
    "reportId": 1,
    "performedByUserId": 1,
    "actionType": "VOTE_CAST",
    "reason": "User cast UPVOTE",
    "createdAt": "2026-04-02T16:05:00"
  },
  {
    "actionId": 3,
    "reportId": 1,
    "performedByUserId": 1,
    "actionType": "VOTE_CHANGED",
    "reason": "User changed vote to DOWNVOTE",
    "createdAt": "2026-04-02T16:06:00"
  },
  {
    "actionId": 4,
    "reportId": 1,
    "performedByUserId": 1,
    "actionType": "VOTE_REMOVED",
    "reason": "User removed vote DOWNVOTE",
    "createdAt": "2026-04-02T16:07:00"
  },
  {
    "actionId": 5,
    "reportId": 1,
    "performedByUserId": 2,
    "actionType": "VERIFY",
    "reason": "Confirmed by moderator after review.",
    "createdAt": "2026-04-02T16:10:00"
  }
]
```

```json
[
  {
    "actionId": 6,
    "reportId": 3,
    "performedByUserId": 2,
    "actionType": "REJECT",
    "reason": "Misleading or invalid report.",
    "createdAt": "2026-04-02T16:15:00"
  }
]
```

```json
[
  {
    "actionId": 7,
    "reportId": 4,
    "performedByUserId": 2,
    "actionType": "MARK_DUPLICATE",
    "reason": "Same incident already covered by report 1.",
    "createdAt": "2026-04-02T16:20:00"
  }
]
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Moderation history returned|Inline|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|none|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|None|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|None|

### Responses Data Schema

HTTP Status Code **200**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» actionId|integer(int64)|true|none||none|
|» reportId|integer(int64)|true|none||none|
|» performedByUserId|integer(int64)¦null|false|none||none|
|» actionType|[ModerationActionType](#schemamoderationactiontype)|true|none||none|
|» reason|string¦null|false|none||none|
|» createdAt|string(date-time)|true|none||none|

#### Enum

|Name|Value|
|---|---|
|actionType|VERIFY|
|actionType|REJECT|
|actionType|MARK_DUPLICATE|
|actionType|AUTO_MARK_DUPLICATE|
|actionType|VOTE_CAST|
|actionType|VOTE_CHANGED|
|actionType|VOTE_REMOVED|
|actionType|AUTO_VERIFY|
|actionType|AUTO_REJECT|
|actionType|RESET_TO_PENDING|

## POST Verify a report

POST /api/v1/reports/{reportId}/moderation/verify

Protected endpoint for moderators or admins. Sets the target report status to VERIFIED and records an auditable moderation action.

Authentication:
This endpoint requires a Bearer JWT access token in the Authorization header.

Authorization: Bearer <access_token>

Only moderator or admin roles are allowed to perform this action.

> Body Parameters

```json
{
  "reason": "Confirmed by moderator after review."
}
```

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|reportId|path|integer| yes |Target report ID.|
|body|body|object| yes |none|
|» reason|body|string| yes |none|

> Response Examples

> Report verified

```json
{
  "reportId": 1,
  "message": "Report verified successfully.",
  "duplicateOfReportId": null,
  "status": "VERIFIED"
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Report verified|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|None|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|none|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|None|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|None|

### Responses Data Schema

HTTP Status Code **200**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» reportId|integer(int64)|true|none||none|
|» status|[ReportStatus](#schemareportstatus)|true|none||none|
|» duplicateOfReportId|integer(int64)¦null|false|none||none|
|» message|string|true|none||none|

#### Enum

|Name|Value|
|---|---|
|status|PENDING|
|status|VERIFIED|
|status|REJECTED|
|status|DUPLICATE|

## POST Reject a report

POST /api/v1/reports/{reportId}/moderation/reject

Protected endpoint for moderators or admins. Sets the target report status to REJECTED and records an auditable moderation action.

Authentication:
This endpoint requires a Bearer JWT access token in the Authorization header.

Authorization: Bearer <access_token>

Only moderator or admin roles are allowed to perform this action.

> Body Parameters

```json
{
  "reason": "Misleading or invalid report."
}
```

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|reportId|path|integer| yes |Target report ID.|
|body|body|object| yes |none|
|» reason|body|string| no |none|

> Response Examples

> 200 Response

```json
{
  "reportId": 3,
  "message": "Report rejected successfully.",
  "duplicateOfReportId": null,
  "status": "REJECTED"
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Report rejected|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|None|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|none|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|None|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|None|

### Responses Data Schema

HTTP Status Code **200**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» reportId|integer(int64)|true|none||none|
|» status|[ReportStatus](#schemareportstatus)|true|none||none|
|» duplicateOfReportId|integer(int64)¦null|false|none||none|
|» message|string|true|none||none|

#### Enum

|Name|Value|
|---|---|
|status|PENDING|
|status|VERIFIED|
|status|REJECTED|
|status|DUPLICATE|

## POST Mark a report as duplicate

POST /api/v1/reports/{reportId}/moderation/duplicate

Protected endpoint for moderators or admins. Marks the target report as DUPLICATE, links it to an existing report, and records an auditable moderation action.

Authentication:
This endpoint requires a Bearer JWT access token in the Authorization header.

Authorization: Bearer <access_token>

Only moderator or admin roles are allowed to perform this action.

> Body Parameters

```json
{
  "duplicateOfReportId": 1,
  "reason": "Same incident already covered by report 1."
}
```

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|reportId|path|integer| yes |Target report ID.|
|body|body|[ModerationDecisionRequest](#schemamoderationdecisionrequest)| yes |none|

> Response Examples

> 200 Response

```json
{
  "reportId": 4,
  "message": "Report marked as duplicate successfully.",
  "duplicateOfReportId": 1,
  "status": "DUPLICATE"
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Report marked as duplicate|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|None|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|none|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|None|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|None|

### Responses Data Schema

HTTP Status Code **200**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» reportId|integer(int64)|true|none||none|
|» status|[ReportStatus](#schemareportstatus)|true|none||none|
|» duplicateOfReportId|integer(int64)¦null|true|none||none|
|» message|string|true|none||none|

#### Enum

|Name|Value|
|---|---|
|status|PENDING|
|status|VERIFIED|
|status|REJECTED|
|status|DUPLICATE|

# sana

## POST Login

POST /api/v1/auth/authenticate

Authenticates a user and returns JWT access and refresh tokens. Use the returned accessToken as Bearer token for all subsequent requests.

> Body Parameters

```json
{
  "email": "sana@wasel.com",
  "password": "1234"
}
```

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|body|body|object| yes |none|

> Response Examples

> 200 Response

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJ0b2tlblR5cGUiOiJhY2Nlc3MiLCJ1c2VySWQiOjIwMiwic3ViIjoic2FuYUB3YXNlbC5jb20iLCJpYXQiOjE3NzY2Nzk0NTIsImV4cCI6MTc3NjY4MDM1Mn0.YS_j-3bO3FeFvx3CQvSFfgm_5YJp0iAHYZalwmS9dvs",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJ0b2tlblR5cGUiOiJyZWZyZXNoIiwic3ViIjoic2FuYUB3YXNlbC5jb20iLCJpYXQiOjE3NzY2Nzk0NTIsImV4cCI6MTc3NzI4NDI1Mn0.OHlmdLLIJzdRW6R7c-Uucn6R43_K9IfM6faywp-YCkI",
  "userId": 202,
  "role": "ADMIN",
  "email": "sana@wasel.com"
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

## GET Route estimation with avoidAreas

GET /api/v1/routes

Estimates a route while avoiding specific geographic areas by name. Uses geocoding to resolve area names to coordinates and checks if they intersect the route. Returns avoided areas and factors in metadata.

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|originLat|query|string| no |none|
|originLon|query|string| no |none|
|destinationLat|query|string| no |none|
|destinationLon|query|string| no |none|
|avoidAreas|query|array[string]| no |none|

> Response Examples

> 200 Response

```json
{
  "estimatedDistance": 0,
  "estimatedDuration": 0,
  "metadata": {
    "affectedCheckpoints": null,
    "avoidedAreas": [
      "string"
    ],
    "factors": [
      "string"
    ],
    "routeModified": true
  }
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

HTTP Status Code **200**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» estimatedDistance|number|true|none||none|
|» estimatedDuration|number|true|none||none|
|» metadata|object|true|none||none|
|»» affectedCheckpoints|null|true|none||none|
|»» avoidedAreas|[string]|true|none||none|
|»» factors|[string]|true|none||none|
|»» routeModified|boolean|true|none||none|

## GET Get All Checkpoints

GET /api/v1/checkpoints

Returns a list of all checkpoints in the system with their current status (OPEN, CLOSED, DELAYED). Provides the centralized registry of all checkpoints and road conditions.

> Response Examples

> 200 Response

```json
[
  {
    "currentStatus": "OPEN",
    "description": "Military checkpoint south of Hebron region",
    "id": 1,
    "latitude": 31.234,
    "longitude": 35.189,
    "name": "Surra Checkpoint"
  },
  {
    "currentStatus": "OPEN",
    "description": "Main checkpoint south of Nablus on Route 60",
    "id": 2,
    "latitude": 32.195,
    "longitude": 35.264,
    "name": "Hawwara Checkpoint"
  },
  {
    "currentStatus": "OPEN",
    "description": "Checkpoint northwest of Nablus near Route 57",
    "id": 3,
    "latitude": 32.234,
    "longitude": 35.156,
    "name": "Deir Sharaf Checkpoint"
  },
  {
    "currentStatus": "DELAYED",
    "description": "Major crossing between Ramallah and Jerusalem",
    "id": 4,
    "latitude": 31.867,
    "longitude": 35.215,
    "name": "Qalandiya Checkpoint"
  },
  {
    "currentStatus": "OPEN",
    "description": "Checkpoint on Route 60 between Jerusalem and Bethlehem",
    "id": 5,
    "latitude": 31.536,
    "longitude": 35.095,
    "name": "Container Checkpoint"
  },
  {
    "currentStatus": "CLOSED",
    "description": "Eastern checkpoint controlling access east of Nablus",
    "id": 6,
    "latitude": 32.204,
    "longitude": 35.329,
    "name": "Beit Furik Checkpoint"
  },
  {
    "currentStatus": "OPEN",
    "description": "Junction checkpoint south of Nablus on Route 60",
    "id": 7,
    "latitude": 32.096,
    "longitude": 35.191,
    "name": "Zaatara Checkpoint"
  },
  {
    "currentStatus": "OPEN",
    "description": "District Coordination Office checkpoint near Ramallah",
    "id": 8,
    "latitude": 31.906,
    "longitude": 35.204,
    "name": "DCO Checkpoint"
  },
  {
    "currentStatus": "DELAYED",
    "description": "Checkpoint near southwestern Jerusalem entrance",
    "id": 9,
    "latitude": 31.711,
    "longitude": 35.189,
    "name": "Gilo Checkpoint"
  },
  {
    "currentStatus": "OPEN",
    "description": "Main pedestrian and vehicle crossing near Bethlehem",
    "id": 10,
    "latitude": 31.713,
    "longitude": 35.206,
    "name": "Bethlehem 300 Checkpoint"
  },
  {
    "currentStatus": "CLOSED",
    "description": "Checkpoint on Route 57 near Tulkarm district",
    "id": 11,
    "latitude": 32.342,
    "longitude": 35.023,
    "name": "Jubara Checkpoint"
  },
  {
    "currentStatus": "OPEN",
    "description": "Checkpoint east of Tulkarm on Route 57",
    "id": 12,
    "latitude": 32.302,
    "longitude": 35.087,
    "name": "Anabta Checkpoint"
  },
  {
    "currentStatus": "OPEN",
    "description": "Secondary checkpoint near Huwwara village south of Nablus",
    "id": 13,
    "latitude": 32.188,
    "longitude": 35.271,
    "name": "Huwwara Bypass Checkpoint"
  },
  {
    "currentStatus": "CLOSED",
    "description": "Checkpoint near Qalqilya district on Route 55",
    "id": 14,
    "latitude": 32.228,
    "longitude": 35.091,
    "name": "Jit Checkpoint"
  },
  {
    "currentStatus": "OPEN",
    "description": "Jordan Valley checkpoint on Route 90",
    "id": 15,
    "latitude": 32.128,
    "longitude": 35.402,
    "name": "Al-Hamra Checkpoint"
  },
  {
    "currentStatus": "DELAYED",
    "description": "Checkpoint in northern Jordan Valley controlling access",
    "id": 16,
    "latitude": 32.226,
    "longitude": 35.465,
    "name": "Tayasir Checkpoint"
  },
  {
    "currentStatus": "OPEN",
    "description": "Western checkpoint controlling exit from Nablus",
    "id": 17,
    "latitude": 32.255,
    "longitude": 35.192,
    "name": "Beit Iba Checkpoint"
  },
  {
    "currentStatus": "CLOSED",
    "description": "Military checkpoint near Jenin on Route 60",
    "id": 18,
    "latitude": 32.333,
    "longitude": 35.181,
    "name": "Salem Checkpoint"
  },
  {
    "currentStatus": "OPEN",
    "description": "Coordination checkpoint south of Bethlehem area",
    "id": 19,
    "latitude": 31.659,
    "longitude": 35.108,
    "name": "Etzion DCO Checkpoint"
  },
  {
    "currentStatus": "OPEN",
    "description": "Checkpoint at the entrance to Shufat refugee camp",
    "id": 20,
    "latitude": 31.819,
    "longitude": 35.233,
    "name": "Shufat Refugee Camp Checkpoint"
  }
]
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

## GET Get Checkpoint by ID

GET /api/v1/checkpoints/3

Returns detailed information about a specific checkpoint identified by its ID.

> Response Examples

> 200 Response

```json
{
  "currentStatus": "string",
  "description": "string",
  "id": 0,
  "latitude": 0,
  "longitude": 0,
  "name": "string"
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

HTTP Status Code **200**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» currentStatus|string|true|none||none|
|» description|string|true|none||none|
|» id|integer|true|none||none|
|» latitude|number|true|none||none|
|» longitude|number|true|none||none|
|» name|string|true|none||none|

## PATCH Update Checkpoint Status

PATCH /api/v1/checkpoints/1/status

Updates the current status of a checkpoint (OPEN, CLOSED, DELAYED). Every status change is automatically recorded in CheckpointStatusHistory with timestamp and the user who made the change.

> Body Parameters

```json
{
  "status": "DELAYED"
}
```

### Params

|Name|Location|Type|Required|Description|
|---|---|---|---|---|
|id|query|string| no |none|
|status|query|string| no |none|
|body|body|object| yes |none|

> Response Examples

> 200 Response

```json
{
  "currentStatus": "string",
  "description": "string",
  "id": 0,
  "latitude": 0,
  "longitude": 0,
  "name": "string"
}
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

HTTP Status Code **200**

|Name|Type|Required|Restrictions|Title|description|
|---|---|---|---|---|---|
|» currentStatus|string|true|none||none|
|» description|string|true|none||none|
|» id|integer|true|none||none|
|» latitude|number|true|none||none|
|» longitude|number|true|none||none|
|» name|string|true|none||none|

## GET Get Checkpoint Status History

GET /api/v1/checkpoints/1/history

Returns the full history of status changes for a specific checkpoint, ordered from newest to oldest. Each record includes the new status, timestamp, and who made the change — enabling audit and performance monitoring.

> Response Examples

> 200 Response

```json
[
  {
    "status": "DELAYED",
    "statusId": 1,
    "updatedAt": "2026-04-20T11:15:09.442005",
    "updatedById": 203,
    "updatedByName": "Admin User"
  }
]
```

### Responses

|HTTP Status Code |Meaning|Description|Data schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### Responses Data Schema

# Data Schema

<h2 id="tocS_ModerationDecisionRequest">ModerationDecisionRequest</h2>

<a id="schemamoderationdecisionrequest"></a>
<a id="schema_ModerationDecisionRequest"></a>
<a id="tocSmoderationdecisionrequest"></a>
<a id="tocsmoderationdecisionrequest"></a>

```json
{
  "reason": "string",
  "duplicateOfReportId": 0
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|reason|string|false|none||none|
|duplicateOfReportId|integer(int64)¦null|false|none||none|

<h2 id="tocS_VoteResponse">VoteResponse</h2>

<a id="schemavoteresponse"></a>
<a id="schema_VoteResponse"></a>
<a id="tocSvoteresponse"></a>
<a id="tocsvoteresponse"></a>

```json
{
  "reportId": 0,
  "userId": 0,
  "userVote": "UPVOTE",
  "upvotes": 0,
  "downvotes": 0,
  "score": 0,
  "reportStatus": "PENDING",
  "message": "string"
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|reportId|integer(int64)|false|none||none|
|userId|integer(int64)|false|none||none|
|userVote|[VoteType](#schemavotetype)|false|none||none|
|upvotes|integer(int64)|false|none||none|
|downvotes|integer(int64)|false|none||none|
|score|integer(int64)|false|none||none|
|reportStatus|[ReportStatus](#schemareportstatus)|false|none||none|
|message|string|false|none||none|

<h2 id="tocS_AlertSubscriptionRequest">AlertSubscriptionRequest</h2>

<a id="schemaalertsubscriptionrequest"></a>
<a id="schema_AlertSubscriptionRequest"></a>
<a id="tocSalertsubscriptionrequest"></a>
<a id="tocsalertsubscriptionrequest"></a>

```json
{
  "placeName": "string",
  "radiusKm": 0.1,
  "category": "CLOSURE",
  "active": true
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|placeName|string|true|none||none|
|radiusKm|number(double)¦null|false|none||none|
|category|[IncidentCategory](#schemaincidentcategory)|true|none||none|
|active|boolean¦null|false|none||none|

<h2 id="tocS_IncidentCategory">IncidentCategory</h2>

<a id="schemaincidentcategory"></a>
<a id="schema_IncidentCategory"></a>
<a id="tocSincidentcategory"></a>
<a id="tocsincidentcategory"></a>

```json
"CLOSURE"

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|*anonymous*|string|false|none||none|

#### Enum

|Name|Value|
|---|---|
|*anonymous*|CLOSURE|
|*anonymous*|ACCIDENT|
|*anonymous*|WEATHER_HAZARD|
|*anonymous*|DELAY|
|*anonymous*|ROAD_WORKS|
|*anonymous*|OTHER|

<h2 id="tocS_ReportStatus">ReportStatus</h2>

<a id="schemareportstatus"></a>
<a id="schema_ReportStatus"></a>
<a id="tocSreportstatus"></a>
<a id="tocsreportstatus"></a>

```json
"PENDING"

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|*anonymous*|string|false|none||none|

#### Enum

|Name|Value|
|---|---|
|*anonymous*|PENDING|
|*anonymous*|VERIFIED|
|*anonymous*|REJECTED|
|*anonymous*|DUPLICATE|

<h2 id="tocS_VoteType">VoteType</h2>

<a id="schemavotetype"></a>
<a id="schema_VoteType"></a>
<a id="tocSvotetype"></a>
<a id="tocsvotetype"></a>

```json
"UPVOTE"

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|*anonymous*|string|false|none||none|

#### Enum

|Name|Value|
|---|---|
|*anonymous*|UPVOTE|
|*anonymous*|DOWNVOTE|

<h2 id="tocS_ModerationActionType">ModerationActionType</h2>

<a id="schemamoderationactiontype"></a>
<a id="schema_ModerationActionType"></a>
<a id="tocSmoderationactiontype"></a>
<a id="tocsmoderationactiontype"></a>

```json
"VERIFY"

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|*anonymous*|string|false|none||none|

#### Enum

|Name|Value|
|---|---|
|*anonymous*|VERIFY|
|*anonymous*|REJECT|
|*anonymous*|MARK_DUPLICATE|
|*anonymous*|AUTO_MARK_DUPLICATE|
|*anonymous*|VOTE_CAST|
|*anonymous*|VOTE_CHANGED|
|*anonymous*|VOTE_REMOVED|
|*anonymous*|AUTO_VERIFY|
|*anonymous*|AUTO_REJECT|
|*anonymous*|RESET_TO_PENDING|

<h2 id="tocS_DetailedError">DetailedError</h2>

<a id="schemadetailederror"></a>
<a id="schema_DetailedError"></a>
<a id="tocSdetailederror"></a>
<a id="tocsdetailederror"></a>

```json
{
  "timestamp": "string",
  "error": "string",
  "message": "string"
}

```

### Attribute

|Name|Type|Required|Restrictions|Title|Description|
|---|---|---|---|---|---|
|timestamp|string|false|none||none|
|error|string|false|none||none|
|message|string|false|none||none|

