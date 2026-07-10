# Architecture Overview

This project adheres to **Clean Architecture** principles and the standard **Layered Architecture** pattern common in enterprise Spring Boot applications. The design ensures clear separation of concerns, high testability, and robust error handling.

## Layered Architecture

1. **Controller Layer (`/controller`)**: 
   - Exposes REST endpoints.
   - Responsible for HTTP request routing and extracting JSON payloads into DTOs.
   - Contains **no business logic**.
   - Triggers Jakarta Validation (`@Valid`).

2. **Service Layer (`/service` and `/service/impl`)**:
   - The core of the application.
   - Contains all business logic, validation rules, and transactional boundaries (`@Transactional`).
   - Translates DTOs to Entities and vice-versa.
   - Interfaces are defined first for loose coupling and easy mocking during tests.

3. **Repository Layer (`/repository`)**:
   - Extends Spring Data JPA interfaces (`JpaRepository`).
   - Handles all interactions with the MySQL database.
   - Uses derived query methods and native `@Query` only when necessary.

4. **DTO Layer (`/dto`)**:
   - Utilizes Java 14+ `record` constructs for immutable data transfer objects.
   - Separates Request and Response payloads to decouple the API contract from the Database Schema.

5. **Exception Handling (`/exception`)**:
   - Centralized error processing via `@RestControllerAdvice`.
   - Translates domain exceptions (e.g., `ResourceNotFoundException`, `AppointmentConflictException`) into standard JSON `ErrorResponse` objects with appropriate HTTP status codes.

## Request Flow

1. Client sends HTTP Request with JSON payload and JWT Bearer token.
2. **Security Filter Chain** intercepts the request:
   - Evaluates CORS.
   - `JwtAuthenticationFilter` validates the token and sets the `SecurityContext`.
3. **DispatcherServlet** routes to the appropriate **Controller**.
4. Controller receives the **Request DTO** and validates it.
5. Controller delegates to the **Service** layer.
6. Service executes business rules and calls the **Repository**.
7. Repository queries the **Database** (managed by Flyway) and returns **Entities**.
8. Service maps Entities to **Response DTOs** and returns them to the Controller.
9. Controller returns a `ResponseEntity` with the proper HTTP status code.

## Security Flow

The system employs **Stateless JWT Authentication**.
- There is no server-side session state (`SessionCreationPolicy.STATELESS`).
- Upon successful login, the server issues a short-lived `access_token` and a long-lived `refresh_token`.
- The client includes the `access_token` in the `Authorization: Bearer <token>` header for all protected endpoints.
- If the access token expires, the client calls `/api/auth/refresh` using the refresh token to obtain a new access token without requiring the user to log in again.

## Package Structure

```text
src/main/java/com/healthcare/appointment/
├── config/         # App-wide configurations (OpenAPI, Application Properties)
├── controller/     # REST API Endpoints
├── dto/            # Data Transfer Objects (Requests & Responses)
├── entity/         # JPA Domain Entities
├── enums/          # Enumerations (Role, AppointmentStatus, etc.)
├── exception/      # Custom Exceptions & Global Exception Handler
├── repository/     # Spring Data JPA Interfaces
├── security/       # JWT Filters, EntryPoints, UserDetails
└── service/        # Business Logic Interfaces & Implementations
```
