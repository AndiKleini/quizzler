# syntax=docker/dockerfile:1

# --- Build stage -----------------------------------------------------------
# Spring Boot 2.7 / Java 17 (kept in sync with quizzler-api).
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Resolve dependencies first so they are cached when only sources change.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# --- Runtime stage ---------------------------------------------------------
FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

# Run as an unprivileged user.
RUN useradd --system --uid 1001 spring
USER spring

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]