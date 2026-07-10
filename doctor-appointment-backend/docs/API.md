# API Documentation

The complete, interactive API documentation is automatically generated using OpenAPI (Swagger 3).
Once the application is running, navigate to:
**`http://localhost:8080/swagger-ui.html`**

## Endpoint Summary

### 1. Authentication (`/api/auth`)
| Method | Endpoint | Description | Security |
|--------|----------|-------------|----------|
| POST | `/register` | Register a new user | Public |
| POST | `/login` | Authenticate and obtain JWT | Public |
| POST | `/refresh` | Obtain a new access token | Public |
| POST | `/logout` | Invalidate refresh token | Bearer |

### 2. Doctors (`/api/v1/doctors`)
| Method | Endpoint | Description | Security |
|--------|----------|-------------|----------|
| POST | `/profile` | Create doctor profile | Bearer (DOCTOR) |
| PUT | `/profile` | Update doctor profile | Bearer (DOCTOR) |
| GET | `/profile/me` | Get own profile | Bearer (DOCTOR) |
| GET | `/` | Search/list doctors (paginated) | Bearer |

### 3. Patients (`/api/v1/patients`)
| Method | Endpoint | Description | Security |
|--------|----------|-------------|----------|
| POST | `/profile` | Create patient profile | Bearer (PATIENT) |
| PUT | `/profile` | Update patient profile | Bearer (PATIENT) |
| GET | `/profile/me` | Get own profile | Bearer (PATIENT) |

### 4. Doctor Availability (`/api/v1/availability`)
| Method | Endpoint | Description | Security |
|--------|----------|-------------|----------|
| POST | `/` | Add working hours | Bearer (DOCTOR) |
| GET | `/doctor/{id}`| Get doctor schedule | Bearer |

### 5. Appointments (`/api/v1/appointments`)
| Method | Endpoint | Description | Security |
|--------|----------|-------------|----------|
| POST | `/` | Book an appointment | Bearer (PATIENT)|
| GET | `/patient/me` | Get patient appointments | Bearer (PATIENT)|
| GET | `/doctor/me` | Get doctor appointments | Bearer (DOCTOR) |
| PATCH | `/{id}/status`| Update status (e.g., CANCEL) | Bearer (DOCTOR/ADMIN) |

## Standard Error Response Format

Whenever an API request fails (Validation, Not Found, Conflict, etc.), the server guarantees a consistent JSON error response structure:

```json
{
  "timestamp": "2026-07-10T14:00:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/auth/register",
  "details": {
    "email": "Invalid email format",
    "password": "Password is required"
  }
}
```
