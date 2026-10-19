# Room API

The Room API is a layered RESTful web service for managing room resources, built with Spring Boot using a three-layer architecture (Controller, Service, Repository).

## Endpoints

| Method | Path | Status Code | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/rooms` | `200 OK` | Retrieves all rooms (supports `minCapacity` and `keyword` filters) |
| `GET` | `/api/rooms/{id}` | `200 OK` / `404 Not Found` | Retrieves a single room by its ID |
| `POST` | `/api/rooms` | `201 Created` / `400 Bad Request` | Creates a new room (returns `Location` header; 400 if capacity < 1 or > 20) |
| `PUT` | `/api/rooms/{id}` | `200 OK` / `400 Bad Request` / `404 Not Found` | Updates a room (400 if capacity < 1 or > 20; 404 if not found) |
| `DELETE` | `/api/rooms/{id}` | `204 No Content` / `404 Not Found` | Deletes a room by its ID |

## Environment & Requirements

- **JDK Version**: Java 21
- **API Documentation**: Available at `http://localhost:8080/swagger-ui.html`

## How to Run

1. In IntelliJ IDEA, run `RoomApiApplication.java` (or execute `./gradlew bootRun` in the terminal).
2. The server runs on `http://localhost:8080`.

## AI Declaration

Service and layer refactoring written with assistance from AI, then verified and tested by me.