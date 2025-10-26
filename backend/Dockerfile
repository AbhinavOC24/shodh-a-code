# === Stage 1: build jar ===
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# === Stage 2: runtime ===
FROM openjdk:17-jdk-slim
WORKDIR /app

# 🧩 Install Docker CLI so ProcessBuilder("docker", ...) works
RUN apt-get update && apt-get install -y docker.io && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Copy built jar from builder
COPY --from=builder /app/target/*.jar app.jar

# Optional: pre-pull judge images (saves first-run time)
RUN docker pull gcc:13.2.0 || true && \
    docker pull azul/zulu-openjdk:17 || true && \
    docker pull python:3.11-alpine || true

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
