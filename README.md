# Room API

The Room API is a layered RESTful web service for managing room resources, built with Spring Boot using a three-layer architecture (Controller, Service, Repository) backed by MySQL and Spring Data JPA.

## Database

- **Database Engine**: MySQL 8.x
- **ORM / Persistence**: Spring Data JPA / Hibernate
- **Schema Management**: Managed via MySQL (`ddl-auto=validate`)

### Tables and Relationships
- **room**: `id` (PK, BIGINT AUTO_INCREMENT), `name` (VARCHAR), `capacity` (INT, CHECK 1-20)
- **reservation**: `id` (PK, BIGINT AUTO_INCREMENT), `room_id` (FK -> room.id), `reserved_by` (VARCHAR), `start_time` (TIMESTAMP), `end_time` (TIMESTAMP)
- **Relationship**: One-to-Many (`room` 1 : N `reservation`).

## Endpoints

| Method | Path | Status Code | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/rooms` | `200 OK` | Retrieves all rooms (supports `minCapacity` and `keyword` filters) |
| `GET` | `/api/rooms/{id}` | `200 OK` / `404 Not Found` | Retrieves a single room by ID |
| `POST` | `/api/rooms` | `201 Created` / `400 Bad Request` | Creates a new room (returns `Location` header; 400 if capacity < 1 or > 20) |
| `PUT` | `/api/rooms/{id}` | `200 OK` / `400 Bad Request` / `404 Not Found` | Updates a room (400 if capacity < 1 or > 20; 404 if not found) |
| `DELETE` | `/api/rooms/{id}` | `204 No Content` / `404 Not Found` | Deletes a room by ID |

## Environment & Requirements

- **JDK Version**: Java 21
- **API Documentation**: Available at `http://localhost:8080/swagger-ui.html`

## How to Run

1. Ensure MySQL is running on `localhost:3306` with the `roomdb` database created.
2. In IntelliJ IDEA, run `RoomApiApplication.java` (or execute `./gradlew bootRun` in the terminal).
3. The server runs on `http://localhost:8080`.

## AI Declaration

Architecture refactored to Spring Data JPA and MySQL with assistance from AI, then tested and verified by me.