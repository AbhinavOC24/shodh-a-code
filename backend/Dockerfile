FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-slim
WORKDIR /app


RUN apt-get update && apt-get install -y docker.io && \
    apt-get clean && rm -rf /var/lib/apt/lists/*


COPY --from=builder /app/target/*.jar app.jar


RUN docker pull gcc:13.2.0 || true && \
    docker pull azul/zulu-openjdk:17 || true && \
    docker pull python:3.11-alpine || true

    
EXPOSE 8080
CMD bash -c "rm -rf /tmp/judge_tmp/* /app/data/* 2>/dev/null || true && java -jar app.jar"

