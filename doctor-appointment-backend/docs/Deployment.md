# Deployment Guide

This document outlines the procedures for running the application locally, via Docker, and deploying to a cloud platform like Render.

## 1. Local Development (Native)

Ensure you have installed:
- Java 17
- Maven 3.9+
- MySQL 8.0

**Steps:**
1. Clone the repository.
2. Start your local MySQL server and create a database named `doctor_appointment_db`.
3. Copy `.env.example` to `.env` (or configure your IDE) and set the database credentials.
4. Run the application using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```
5. Flyway will automatically create the tables on startup.
6. Access Swagger at `http://localhost:8080/swagger-ui.html`.

## 2. Docker Compose (Local Testing)

To run the entire stack (Database + API) in isolated containers without installing MySQL or Java on your machine:

1. Ensure Docker Desktop is running.
2. From the project root, run:
   ```bash
   docker-compose up --build -d
   ```
3. To view the application logs:
   ```bash
   docker-compose logs -f backend
   ```
4. To stop the stack and preserve data:
   ```bash
   docker-compose down
   ```
5. To stop and wipe the database:
   ```bash
   docker-compose down -v
   ```

## 3. Production Deployment (Render)

This project utilizes **Infrastructure as Code (IaC)** via a `render.yaml` Blueprint for seamless cloud deployment.

### Architecture on Render
- **Database (MySQL)**: Deployed as a Private Service. Not accessible from the internet. Mounted with a 10GB persistent disk.
- **Backend API (Spring Boot)**: Deployed as a Public Web Service. Uses our multi-stage Dockerfile. Connects to the database securely over Render's internal VPC.

### Deployment Steps
1. Push all code to your `main` branch on GitHub.
2. Log in to [Render.com](https://render.com).
3. Click **New** -> **Blueprint**.
4. Connect your GitHub repository.
5. Render will automatically parse `render.yaml` and provision both services.
6. Render will auto-generate secure 256-bit passwords for the database and the JWT Secret.
7. Once deployed, Render will provide a public HTTPS URL (e.g., `https://your-app.onrender.com`).

## Troubleshooting

- **"Communications link failure" on startup**:
  - *Cause*: The backend container started slightly faster than the database container.
  - *Fix*: Wait 30 seconds. In Docker Compose or Render, the backend service will automatically restart and successfully connect.
- **"Public Key Retrieval is not allowed"**:
  - *Fix*: Ensure `allowPublicKeyRetrieval=true` is present in your JDBC URL in `application-prod.yml`.
- **Flyway Checksum Mismatch**:
  - *Cause*: You modified an existing `V*.sql` script after it had already run.
  - *Fix*: Never modify executed scripts. Write a new `V2__...sql` script. For local dev, wipe the database and restart.
