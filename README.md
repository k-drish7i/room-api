# Room API

The Room API is a layered RESTful web service for managing rooms and reservations, built with Spring Boot using a three-layer architecture (Controller, Service, Repository) backed by MySQL, Spring Data JPA, and stateless JWT/Basic Spring Security.

## Authentication

### Public vs. Protected Endpoints
- **Public (No Login Required)**:
    - `GET /api/rooms` and `GET /api/rooms/{id}` (reading room details)
    - `POST /api/auth/signup` (creating a new account)
    - `POST /api/auth/login` (exchanging credentials for a JWT)
    - Swagger UI & OpenAPI docs (`/swagger-ui/**`, `/v3/api-docs/**`)
- **Protected (Authentication Required)**:
    - All `POST`, `PUT`, and `DELETE` endpoints on `/api/rooms/**` and `/api/reservations/**`
    - All `GET` endpoints on `/api/reservations/**`
    - `GET /api/auth/me`

### How to Get and Send a Token
1. **Obtain a Token**: Send a `POST` request to `/api/auth/login` with a JSON body containing `"username"` and `"password"`. The server returns a JSON response with an `accessToken` field.
2. **Send the Token**: Include the token in the `Authorization` HTTP header of subsequent requests using the `Bearer` scheme:
   ```http
   Authorization: Bearer <your-access-token>

(HTTP Basic authentication is also supported for classroom testing).

Sample Accounts
Running reset.sql seeds the app_user table with three sample accounts (passwords are stored exclusively as BCrypt hashes):
Username,Password,Role
mina,mina1234,USER
omar,omar1234,USER
admin,admin1234,ADMIN

Errors
All error responses across the entire API—including security filter rejections—share the uniform ApiError JSON body structure.
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

HTTP Error Status Meanings
Status Code,Status Name,Meaning in This API
400,Bad Request,"Client validation failure: invalid JSON payload, invalid field constraints (@Valid), start time not before end time, missing required parameters, or path variable type mismatch."
401,Unauthorized,"Authentication failure: missing credentials (login required), invalid username/password (wrong username or password), or malformed/expired JWT (token is invalid or expired)."
404,Not Found,Requested entity (room or reservation) or URL path does not exist.
409,Conflict,"Domain logic or database constraint collision: duplicate username on sign-up, reservation time overlap, or attempting to delete a room with active reservations."
500,Internal Server Error,Unhandled server exception (hidden from client for security; actual error logged server-side).

Database Setup
Run these commands in MySQL as root before starting the application:

SQL
CREATE DATABASE roomdb;
CREATE USER 'roomapp'@'localhost' IDENTIFIED BY 'roomapp1234';
GRANT ALL PRIVILEGES ON roomdb.* TO 'roomapp'@'localhost';

To initialize or reset all tables (room, reservation, and app_user) with sample data, execute reset.sql.

Environment & Requirements
JDK Version: Java 21

JWT Secret: Configured via the JWT_SECRET environment variable (app.jwt.secret), falling back to a development default in application.properties.

API Documentation: Available at http://localhost:8080/swagger-ui.html

How to Run
Ensure MySQL is running on localhost:3306 with roomdb configured and reset.sql executed.

In IntelliJ IDEA, run RoomApiApplication.java (or run ./gradlew bootRun).

The server runs on http://localhost:8080.

AI Declaration
Spring Security filter chain configuration, BCrypt password encoding, JWT encoder/decoder setup, and security entry-point exception handling implemented with assistance from AI, verified and tested by me.