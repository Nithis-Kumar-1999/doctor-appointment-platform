# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-07-24

### Added
- **Module 1: Authentication System**
  - JWT storage in AuthContext.
  - Axios interceptors for `Authorization` headers.
  - Role-based route guards for `PATIENT` and `DOCTOR`.
  - Login and Registration Pages with Zod validation.
- **Module 2: Doctor Dashboard & Profile**
  - Responsive Sidebar & Navbar layout.
  - Doctor Profile Creation and Management.
  - Backend integration via `GET /api/v1/doctors/profile/me`.
- **Module 3: Doctor Availability**
  - Interactive grid to submit weekly availability schedules.
  - Validation to prevent overlapping times or negative durations.
- **Module 4: Patient Dashboard & Doctor Search**
  - Dedicated Patient Dashboard metrics.
  - Doctor Search Engine utilizing `GET /api/v1/doctors` with Specialty filters.
  - Responsive Doctor Profile Cards and Doctor Details view.
- **Module 5: Appointment Booking & Management**
  - Smart interactive `SlotPicker` that generates valid time slots dynamically.
  - Appointment Booking Integration (`POST /api/v1/appointments`).
  - Doctor Appointment Management (Sorting, Filtering, Status updates).
  - Patient Appointment History and Cancellation functionality.

### Changed
- Complete refactor to Feature-Based folder structure.
- Upgraded to Material UI v6 Grid API.
- Replaced Redux with React Query for efficient server state caching.

### Fixed
- Fixed fast refresh warnings by splitting Context hooks.
- Eliminated all unused imports and ESLint warnings.
- Achieved zero TypeScript compiler errors under strict mode.
