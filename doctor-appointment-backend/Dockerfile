# ==========================================
# STAGE 1: Build Stage (Heavy, tooling included)
# ==========================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy only the POM file first to cache the Maven dependencies
# This speeds up builds significantly if pom.xml hasn't changed
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the actual source code
COPY src ./src

# Package the application (skip tests to speed up Docker build; tests should run in CI/CD)
RUN mvn clean package -DskipTests -B

# ==========================================
# STAGE 2: Runtime Stage (Lightweight, production ready)
# ==========================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# SECURITY: Run as a non-root user
# Creating a system group 'spring' and user 'spring'
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built fat JAR from the builder stage
# Uses a wildcard to avoid hardcoding version numbers
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Configure JVM options optimized for containers
# MaxRAMPercentage=75.0 ensures Java respects Docker's memory limits
# UseG1GC is excellent for REST APIs to prevent long pause times
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+OptimizeStringConcat"

# HEALTHCHECK: Native Docker health check hitting the Spring Actuator endpoint
HEALTHCHECK --interval=30s --timeout=5s --retries=3 --start-period=30s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Launch the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
