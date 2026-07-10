# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-07-10

### Added
- Complete Enterprise Java Full Stack Backend initialization.
- Layered Clean Architecture (Controller, Service, Repository, DTO, Exception, Security).
- JWT Stateless Authentication with Access and Refresh tokens.
- Role-based authorization (ADMIN, DOCTOR, PATIENT).
- Global Exception Handling (@RestControllerAdvice).
- MySQL database integration via Spring Data JPA.
- Database schema versioning with Flyway.
- OpenAPI / Swagger documentation integration.
- Full suite of Integration Tests using MockMvc and @SpringBootTest.
- Docker multi-stage build, docker-compose, and optimized JVM options.
- GitHub Actions CI Pipeline for automated testing and building.
- Infrastructure as Code blueprint (`render.yaml`) for Render deployment.
- Comprehensive API and Architecture documentation.
