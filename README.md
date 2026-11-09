# Room API

The Room API is a layered RESTful web service for managing rooms and reservations, built with Spring Boot using a three-layer architecture (Controller, Service, Repository) backed by MySQL and Spring Data JPA.

## Errors

All error responses across the entire API share the uniform `ApiError` JSON body structure.

### ApiError Shape
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "request has invalid fields",
  "path": "/api/rooms",
  "fieldErrors": [
    {
      "field": "capacity",
      "message": "capacity must be between 1 and 20"
    }
  ]
}

```

### HTTP Error Status Meanings

| Status Code | Status Name | Meaning in This API |
| --- | --- | --- |
| **400** | Bad Request | Client validation failure: invalid JSON payload, invalid field constraints (`@Valid`), start time not before end time, missing required parameters, or path variable type mismatch. |
| **404** | Not Found | Requested entity (room or reservation) or URL path does not exist. |
| **409** | Conflict | Domain logic or database constraint collision: reservation time overlap or attempting to delete a room with active reservations. |
| **500** | Internal Server Error | Unhandled server exception (hidden from client for security; actual error logged server-side). |

## Database Setup

Run these commands in MySQL as root before starting the application:

```sql
CREATE DATABASE roomdb;
CREATE USER 'roomapp'@'localhost' IDENTIFIED BY 'roomapp1234';
GRANT ALL PRIVILEGES ON roomdb.* TO 'roomapp'@'localhost';

```

Then execute `schema.sql` and `data.sql` to initialize the tables and sample data. To reset data at any time, execute `reset.sql`.

## Endpoints

### Rooms (`/api/rooms`)

| Method | Path | Status Code | Description |
| --- | --- | --- | --- |
| `GET` | `/api/rooms` | `200 OK` | Retrieves all rooms (supports `minCapacity` and `keyword` filters) |
| `GET` | `/api/rooms/{id}` | `200 OK` / `404 Not Found` | Retrieves a single room by ID |
| `POST` | `/api/rooms` | `201 Created` / `400 Bad Request` | Creates a new room (returns `Location` header; 400 if validation fails) |
| `PUT` | `/api/rooms/{id}` | `200 OK` / `400 Bad Request` / `404 Not Found` | Updates a room via dirty checking |
| `DELETE` | `/api/rooms/{id}` | `204 No Content` / `404 Not Found` / `409 Conflict` | Deletes a room by ID (409 if room has active reservations) |

### Reservations (`/api/reservations`)

| Method | Path | Status Code | Description |
| --- | --- | --- | --- |
| `GET` | `/api/reservations` | `200 OK` / `400 Bad Request` | Retrieves reservations filtered by `reservedBy` |
| `GET` | `/api/reservations/{id}` | `200 OK` / `404 Not Found` | Retrieves a single reservation by ID |
| `POST` | `/api/reservations` | `201 Created` / `400 Bad Request` / `404 Not Found` / `409 Conflict` | Creates a reservation (checks room existence, time order, and overlap) |
| `DELETE` | `/api/reservations/{id}` | `204 No Content` / `404 Not Found` | Deletes a reservation by ID |

## Environment & Requirements

* **JDK Version**: Java 21
* **API Documentation**: Available at `http://localhost:8080/swagger-ui.html`

## How to Run

1. Ensure MySQL is running on `localhost:3306` with `roomdb` configured.
2. In IntelliJ IDEA, run `RoomApiApplication.java` (or run `./gradlew bootRun`).
3. The server runs on `http://localhost:8080`.

## AI Declaration

Validation annotations, global exception handling advice, DTO isolation, and dirty-checking persistence logic implemented with assistance from AI, verified and tested by me.

```

