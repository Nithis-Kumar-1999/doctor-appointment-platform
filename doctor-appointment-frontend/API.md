# API Documentation

This document outlines the API endpoints integrated into the Doctor Appointment Platform frontend.

## Integrated Endpoints

### Authentication (`/api/auth`)
- `POST /api/auth/register`: Register a new patient or doctor.
- `POST /api/auth/login`: Authenticate and receive a JWT.

### Patients (`/api/v1/patients`)
- `GET /api/v1/patients/profile/me`: Get current patient profile.
- `POST /api/v1/patients/profile`: Create/Update patient profile.

### Doctors (`/api/v1/doctors`)
- `GET /api/v1/doctors`: Paginated search for doctors (filters by specialty).
- `GET /api/v1/doctors/profile/me`: Get current doctor profile.
- `POST /api/v1/doctors/profile`: Create/Update doctor profile.

### Availability (`/api/v1/availability`)
- `GET /api/v1/availability/doctor/{id}`: Get a doctor's weekly schedule.
- `POST /api/v1/availability`: Add a new availability slot.

### Appointments (`/api/v1/appointments`)
- `POST /api/v1/appointments`: Book an appointment.
- `GET /api/v1/appointments/patient/me`: Get patient's appointment history.
- `GET /api/v1/appointments/doctor/me`: Get doctor's scheduled appointments.
- `PATCH /api/v1/appointments/{id}/status`: Update appointment status (Confirm/Complete/Cancel).

## Missing Endpoints (Future Roadmap)

- `GET /api/v1/appointments/doctor/{id}/available-slots`: Needed to prevent booking collisions.
- `PUT /api/v1/availability/{id}`: Needed for editing schedules.
- `DELETE /api/v1/availability/{id}`: Needed for removing schedules.
- `GET /api/v1/doctors/{id}`: Needed for direct linking to a doctor profile.
- `GET /api/v1/admin/users`: Needed for Admin Dashboard.
- `GET /api/v1/admin/stats`: Needed for Admin Dashboard.
