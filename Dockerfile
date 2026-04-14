# ──────────────────────────────────────────────────────────────
# Dockerfile — StructLab / DataDance
# ──────────────────────────────────────────────────────────────
# Multi-stage build for reproducible builds, tests, and headless
# execution.  This is NOT a container for running the JavaFX GUI
# (that requires a real display server).  It IS useful for:
#
#   • Reproducible CI-parity builds on any machine
#   • Running the full test suite in an isolated environment
#   • Producing the packaged JAR in a clean room
#   • Headless terminal-mode execution
#
# Usage:
#   docker build -t structlab .                               # build image
#   docker run --rm structlab                                 # run (headless terminal mode)
#   docker cp $(docker create structlab):/app/structlab.jar . # extract JAR
#
# ──────────────────────────────────────────────────────────────

# ── Stage 1: Build & test ────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Cache dependencies first (layer caching optimisation).
# Only pom.xml changes invalidate this layer.
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

# Copy source and build
COPY src/ src/
RUN mvn -B -ntp clean verify package

# ── Stage 2: Minimal runtime image ──────────────────────────
# Contains only the built JAR — no Maven, no source.
FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

COPY --from=builder /app/target/structlab-*.jar structlab.jar

# Default: run in headless terminal mode.
# Override with `docker run structlab <your-command>`.
ENTRYPOINT ["java", "-jar", "structlab.jar"]
