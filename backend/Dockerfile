# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy pom and go offline for deps cache
COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -B -DskipTests clean package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy built jar
COPY --from=builder /workspace/target/*.jar /app/app.jar

# Environment (override via docker-compose)
ENV SPRING_PROFILES_ACTIVE=docker \
    JAVA_OPTS="-Xms256m -Xmx512m" \
    TZ=Asia/Shanghai

EXPOSE 8080

# Use non-root user for safety
RUN addgroup -S app && adduser -S app -G app
USER app

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]