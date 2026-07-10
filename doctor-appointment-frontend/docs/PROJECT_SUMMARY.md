# 📚 Project Summary & Retrospective

## Problem Statement
The healthcare industry often relies on fragmented, legacy systems for patient management and appointment booking. Patients struggle to find available specialists, while doctors lack streamlined tools to manage their working hours and incoming consultations. This fragmentation leads to high no-show rates and administrative bottlenecks.

## The Solution
An integrated, cloud-native SaaS platform providing dedicated, role-based portals for Patients and Doctors. Patients can search for specialists, view real-time availability, and securely book appointments. Doctors receive a comprehensive dashboard to manage their profiles, dictate weekly schedules, and update appointment statuses (Scheduled -> Confirmed -> Completed).

## Architecture
The system is built on a decoupled, N-Tier architecture, separating the client interface from business logic and data persistence to ensure horizontal scalability and maintainability.

### Backend (Spring Boot & Java)
- **Framework**: Spring Boot 3 with Java 17.
- **Pattern**: Strict adherence to Clean Architecture. Controllers handle HTTP boundaries, Services encapsulate business rules, and Repositories interface with the database.
- **Data Transfer**: Strict boundary enforcement using MapStruct-like DTOs to ensure sensitive entity data (like password hashes) never leaks to the presentation layer.

### Frontend (React & TypeScript)
- **Framework**: React 19 bootstrapped with Vite for instant Hot Module Replacement (HMR).
- **Pattern**: Feature-based folder structure (e.g., `features/auth`, `features/appointment`).
- **State Management**: TanStack Query for server-state caching and React Context for global client state.

### Security
- **Stateless Authentication**: Implemented JSON Web Tokens (JWT) instead of stateful sessions, allowing the backend to scale across multiple instances seamlessly.
- **Token Rotation**: Dual-token architecture (Access Token + Refresh Token) ensuring users stay logged in without compromising long-term security.
- **Protection**: BCrypt password hashing, Spring Security CORS configuration, and Vercel edge-network security headers (`X-Frame-Options`, `X-Content-Type-Options`).

### Performance
- **Database Optimization**: Utilized `@EntityGraph` in Spring Data JPA to prevent N+1 query problems when fetching relational data (e.g., fetching a Doctor along with their Appointments).
- **Frontend Bundle**: Implemented `React.lazy()` and Rollup `manualChunks` to split the JavaScript payload, ensuring sub-50ms initial load times.
- **Web Vitals**: Preconnected Google Fonts and added `display=swap` to eliminate Cumulative Layout Shifts (CLS) and Flash of Unstyled Text (FOUT).

### Deployment
- **Database Evolution**: Migrated schema management from Hibernate's unpredictable `ddl-auto` to **Flyway**, ensuring deterministic, version-controlled database deployments.
- **Containerization**: Packaged the backend into a lightweight Docker container using a multi-stage Dockerfile (Maven Build -> JRE Runtime).
- **CI/CD**: Configured GitHub Actions to automatically run unit and integration tests (JUnit, Vitest) before triggering deployments to Render (Backend) and Vercel (Frontend).

## Testing
- **Backend**: Leveraged JUnit 5 and MockMvc for comprehensive integration testing of the REST Controller layers, verifying JSON payloads and HTTP status codes.
- **Frontend**: Utilized React Testing Library with **MSW (Mock Service Worker)**. MSW intercepted network requests at the browser level, allowing the UI to be tested in complete isolation without a running backend.

## Lessons Learned
1. **The value of DTOs**: Initially, sending entities directly to the frontend seems faster, but it creates a fragile codebase. DTOs act as essential contracts between the frontend and backend.
2. **TanStack Query is a game-changer**: Replacing `useEffect` data-fetching with TanStack Query eliminated massive amounts of boilerplate and race conditions, natively providing `isLoading`, `isError`, and intelligent caching out of the box.
3. **Database versioning is non-negotiable**: Using Flyway early in the project saved countless hours of manually dropping and recreating tables during iterative development.
