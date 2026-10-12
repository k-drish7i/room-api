# Room API

The Room API is a RESTful web service for managing room resources, supporting room creation, retrieval, updates, and deletion.

## Endpoints

| Method | Path | Status Code | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/rooms` | `200 OK` | Retrieves all rooms |
| `GET` | `/api/rooms/{id}` | `200 OK` / `404 Not Found` | Retrieves a single room by its ID |
| `POST` | `/api/rooms` | `201 Created` | Creates a new room and returns a Location header |
| `PUT` | `/api/rooms/{id}` | `200 OK` / `404 Not Found` | Updates an existing room's details |
| `DELETE` | `/api/rooms/{id}` | `204 No Content` / `404 Not Found` | Deletes a room by its ID |

## How to Run

1. In IntelliJ IDEA, run `RoomApiApplication.java` (or execute `./gradlew bootRun` in your terminal).
2. The server runs on `http://localhost:8080`.