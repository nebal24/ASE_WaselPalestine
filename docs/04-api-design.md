# API Design Rationale

**Project: Wasel Palestine**

The Wasel Palestine backend API was designed to provide a secure, scalable, and maintainable system for smart mobility services. The following points explain the rationale behind the chosen API architecture and design decisions.

## 1. Why RESTful APIs?

RESTful architecture was chosen because:

* **Stateless:** Each request contains all necessary information.
* **Scalable:** Easy to deploy across multiple servers.
* **Cacheable:** Improves performance for repeated requests.
* **Simple:** Uses standard HTTP methods and clear resource-based URLs.

## 2. Why Versioning (`/api/v1/`)?

* Allows future updates without breaking older clients.
* Enables smooth migration to newer versions.
* Follows industry best practices used by modern platforms.

## 3. Why JWT Authentication?

| Feature        | Benefit                                           |
| -------------- | ------------------------------------------------- |
| Stateless      | No session storage required on server             |
| Scalable       | Works efficiently across multiple servers         |
| Self-contained | Token stores user ID and role                     |
| Expiration     | Tokens expire after a defined period for security |

JWT was selected because it is lightweight, secure, and ideal for REST APIs.

## 4. HTTP Methods Used

| Method | Usage           | Example                        |
| ------ | --------------- | ------------------------------ |
| GET    | Retrieve data   | `GET /incidents`               |
| POST   | Create resource | `POST /incidents`              |
| PUT    | Full update     | `PUT /incidents/{id}`          |
| PATCH  | Partial update  | `PATCH /incidents/{id}/verify` |
| DELETE | Remove resource | `DELETE /incidents/{id}`       |

## 5. HTTP Status Codes Used

| Code | Meaning      | When Used                         |
| ---- | ------------ | --------------------------------- |
| 200  | OK           | Successful GET, PUT, PATCH        |
| 201  | Created      | Successful POST                   |
| 204  | No Content   | Successful DELETE                 |
| 400  | Bad Request  | Invalid input / validation errors |
| 401  | Unauthorized | Missing or invalid token          |
| 403  | Forbidden    | Insufficient permissions          |
| 404  | Not Found    | Resource does not exist           |
| 409  | Conflict     | Duplicate resource (e.g. email)   |

## 6. Why Role-Based Access Control (RBAC)?

| Role      | Permissions                             | Purpose                       |
| --------- | --------------------------------------- | ----------------------------- |
| USER      | Create reports/incidents, view own data | Standard users                |
| MODERATOR | Verify, close, update incidents         | Trusted operational users     |
| ADMIN     | Full system access                      | Administration and management |

RBAC improves security by limiting actions based on user responsibility.

## 7. Why Filtering, Sorting, and Pagination?

These features were implemented to improve **performance**, **scalability**, and **usability** by allowing clients to request only the data they need.

| Feature        | Benefit                                                                                    |
| -------------- | ------------------------------------------------------------------------------------------ |
| **Filtering**  | Returns only matching records and reduces unnecessary data transfer                        |
| **Sorting**    | Organizes results in a meaningful order for users                                          |
| **Pagination** | Splits large datasets into smaller pages, improving response time and reducing server load |

**Implementation Examples:**

* Filtering: `?category=ACCIDENT`
* Sorting: `?sortBy=createdAt&sortDirection=DESC`
* Pagination: `?page=0&size=10`

## 8. Consistent Error Response Format

All errors follow a unified structure to simplify frontend integration and debugging.

```json
{
  "timestamp": "2026-04-16T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Incident not found with id: 100",
  "path": "/api/v1/incidents/100"
}
```

## 9. Why External API Integration?

External APIs were used for weather data and route estimation instead of rebuilding complex services internally. This reduced development time and improved accuracy using trusted real-time providers.

## 10. Conclusion

The API was designed using modern backend best practices to ensure security, maintainability, scalability, and clean integration with future mobile or web clients.
