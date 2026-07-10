# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-07-10

### Added
- **Backend**: Complete Spring Boot REST API structured with Clean Architecture.
- **Backend**: JWT Stateless Authentication flow with automatic Refresh Token rotation.
- **Backend**: Flyway database migrations establishing Users, Roles, Doctors, Patients, and Appointments schemas.
- **Backend**: Docker multi-stage build support and CI/CD GitHub Actions workflow.
- **Frontend**: React 19 + TypeScript SPA built with Vite.
- **Frontend**: Authentication, Patient, Doctor, and Appointment functional modules.
- **Frontend**: TanStack Query integration for aggressive data caching and server-state management.
- **Frontend**: Multi-step Appointment Booking Wizard with strict Zod validation.
- **Frontend**: Production hardening including `React.lazy()` code splitting, Error Boundaries, and MSW test infrastructure.
- **Documentation**: Extensive portfolio, architectural, and maintenance documentation artifacts.

### Security
- Passwords are encrypted using BCrypt.
- Strict Cross-Origin Resource Sharing (CORS) configurations.
- Vercel edge-network security HTTP headers.

### Changed
- Refactored frontend routes to exclusively use lazy-loading for bundle optimization.
- Optimized JPA Repositories to utilize `@EntityGraph` to resolve N+1 query performance bottlenecks.
