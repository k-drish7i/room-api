# Room API

The Room API is a layered RESTful web service for managing room resources, built with Spring Boot using a three-layer architecture (Controller, Service, Repository) backed by MySQL and Spring Data JPA.

## Database Setup

Run these commands in MySQL as root before starting the application:

```sql
CREATE DATABASE roomdb;
CREATE USER 'roomapp'@'localhost' IDENTIFIED BY 'roomapp1234';
GRANT ALL PRIVILEGES ON roomdb.* TO 'roomapp'@'localhost';

Then execute schema.sql and data.sql to initialize the tables and sample data. To reset data at any time, execute reset.sql.

ENDPOINTS
Method,Path,Status Code,Description
GET,/api/rooms,200 OK,Retrieves all rooms (supports minCapacity and keyword filters)
GET,/api/rooms/{id},200 OK / 404 Not Found,Retrieves a single room by ID
POST,/api/rooms,201 Created / 400 Bad Request,Creates a new room (returns Location header; 400 if capacity < 1 or > 20)
PUT,/api/rooms/{id},200 OK / 400 Bad Request / 404 Not Found,Updates a room (400 if capacity < 1 or > 20; 404 if not found)
DELETE,/api/rooms/{id},204 No Content / 404 Not Found / 409 Conflict,Deletes a room by ID (409 if room has active reservations)

Implementation Notes
409 Conflict on DELETE: Handled in the RoomController by catching DataIntegrityViolationException because the database integrity constraint (foreign key in reservation) refuses deletion of referenced parent records, which represents a resource conflict rather than an internal server bug.

Hibernate PUT Behavior: When executing a PUT request, Hibernate issues a SELECT statement first to verify existence and check the existing entity state before issuing the UPDATE query.

Environment & Requirements
JDK Version: Java 21

API Documentation: Available at http://localhost:8080/swagger-ui.html

How to Run
Ensure MySQL is running on localhost:3306 with roomdb configured.

In IntelliJ IDEA, run RoomApiApplication.java (or run ./gradlew bootRun).

The server runs on http://localhost:8080.

AI Declaration
Database layer, JPA migration, and conflict handling implemented with assistance from AI, verified and tested by me.