# Room API

The Room API is a layered RESTful web service for managing rooms and reservations, built with Spring Boot using a three-layer architecture (Controller, Service, Repository) backed by MySQL, Spring Data JPA, and stateless JWT/Basic Spring Security with role-based and ownership-based authorization.

## Authorization & Access Rules

| Resource / Action | Public (No Login) | Logged-In User (`ROLE_USER`) | Admin (`ROLE_ADMIN`) |
| :--- | :---: | :---: | :---: |
| **Read Rooms** (`GET /api/rooms/**`) | Allowed | Allowed | Allowed |
| **Create / Update / Delete Rooms** (`POST`, `PUT`, `DELETE /api/rooms/**`) | 401 | 403 (`admin role required`) | Allowed |
| **Sign Up & Log In** (`POST /api/auth/signup`, `/login`) | Allowed | Allowed | Allowed |
| **View Current User** (`GET /api/auth/me`) | 401 | Allowed | Allowed |
| **Book a Room** (`POST /api/rooms/{id}/reservations`) | 401 | Allowed (sets caller as owner) | Allowed |
| **List Room Reservations** (`GET /api/rooms/{id}/reservations`) | 401 | Allowed | Allowed |
| **List Own Reservations** (`GET /api/reservations/mine`) | 401 | Allowed (own bookings only) | Allowed (own bookings) |
| **Read / Cancel Reservation** (`GET`, `DELETE /api/reservations/{id}`) | 401 | Owner only (403 otherwise) | Allowed (any reservation) |
| **Reschedule Reservation** (`PUT /api/reservations/{id}`) | 401 | Owner only (403 otherwise) | Owner only (403 if not owner) |

*Note on refusal order:* On reservation item endpoints, existence is checked before ownership (`404 Not Found` before `403 Forbidden`).

## Authentication

### How to Get and Send a Token
1. **Obtain a Token**: Send a `POST` request to `/api/auth/login` with `"username"` and `"password"`. The response returns a signed JWT (`accessToken`).
2. **Send the Token**: Pass the token in the `Authorization` header:
   ```http
   Authorization: Bearer <your-access-token>

Sample Accounts
Running reset.sql seeds the app_user table with three sample accounts (passwords stored as BCrypt hashes):
Username,Password,Role
mina,mina1234,USER
omar,omar1234,USER
admin,admin1234,ADMIN

Errors
All error responses across the API—including security filter rejections—share the uniform ApiError JSON body structure.
{
"status": 403,
"error": "Forbidden",
"message": "admin role required",
"path": "/api/rooms",
"fieldErrors": []
}

HTTP Error Status Meanings
Status Code,Status Name,Meaning in This API
400,Bad Request,"Client validation failure: invalid JSON, @Valid field constraint failure, startTime not before endTime, or path variable type mismatch."
401,Unauthorized,"Authentication failure: missing credentials (login required), bad password (wrong username or password), or invalid/expired JWT (token is invalid or expired)."
403,Forbidden,Authorization failure: non-admin attempting room modification (admin role required) or user accessing/modifying another user's reservation (reservation {id} belongs to another user).
404,Not Found,"Requested room, reservation, or URL path does not exist."
409,Conflict,"Domain or database constraint collision: duplicate username, overlapping reservation time, or deleting a room with active reservations."
500,Internal Server Error,Unhandled server exception (details logged server-side; hidden from client).

Database & Environment Setup
Run these commands in MySQL as root before starting the application:

SQL
CREATE DATABASE roomdb;
CREATE USER 'roomapp'@'localhost' IDENTIFIED BY 'roomapp1234';
GRANT ALL PRIVILEGES ON roomdb.* TO 'roomapp'@'localhost';

To initialize or reset all tables (app_user, room, and reservation) with sample data, execute reset.sql.

Environment Variables
Secrets and origins are read from environment variables with class-only fallback defaults in application.properties:

DB_PASSWORD (default: roomapp1234)

JWT_SECRET (default: week10-dev-only-secret-change-me-0123456789)

CORS_ORIGINS (default: http://localhost:5500,http://127.0.0.1:5500)

How to Run
Ensure MySQL is running on localhost:3306 with roomdb configured and reset.sql executed.

In IntelliJ IDEA, run RoomApiApplication.java (or run ./gradlew bootRun).

The server runs on http://localhost:8080 (Swagger UI at http://localhost:8080/swagger-ui.html).

AI Declaration
Role-based JWT authority mapping, custom 403 AccessDeniedHandler wiring, ownership service checks, self-overlap exclusion query, and CORS configuration implemented with assistance from AI, verified and tested by me.
