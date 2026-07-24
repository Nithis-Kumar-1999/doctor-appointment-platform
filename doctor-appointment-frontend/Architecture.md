# Frontend Architecture

This document describes the architectural decisions and patterns used in the React Frontend.

## 1. Feature-Based Modular Architecture
The codebase is organized into **features** (`src/features`). Each feature encapsulates its own components, hooks, pages, types, and API calls. This ensures high cohesion, loose coupling, and scalability.

- `auth`: Handles Login/Registration and JWT processing.
- `doctor`: Manages Doctor profiles and weekly availability schedules.
- `patient`: Manages Patient profiles and the Doctor Search engine.
- `appointment`: Core booking logic, slot generation, and appointment lifecycle management.
- `common`: Cross-cutting concerns like global Snackbars and generic contexts.

## 2. Data Fetching & State Management
- **React Query**: Replaces Redux for server-state management. Handles caching, background refetching, and pagination efficiently.
- **Axios Interceptors**: The Axios client (`apiClient.ts`) intercepts all outgoing requests to append the JWT `Authorization` header, and handles global 401/403 responses.

## 3. UI System
- **Material UI (MUI)**: Serves as the foundational design system. We utilize MUI's Grid (v2/size API) for responsiveness.
- **Reusable Components**: Generic components like `AppointmentCard`, `AppointmentTable`, and `ConfirmationDialog` receive data via props, completely decoupled from data-fetching hooks to maximize reusability.

## 4. Forms & Validation
- **React Hook Form**: Minimizes re-renders during form input.
- **Zod**: Provides strict, type-safe schema validation before payloads are ever sent to the backend.

## 5. Security Model
- **Protected Routes**: Wraps sensitive pages with a `<ProtectedRoute>` component.
- **Role Guards**: `<RoleGuard>` strictly enforces that only `PATIENT` or `DOCTOR` roles can access their respective dashboard ecosystems.
