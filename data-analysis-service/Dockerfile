# Multi-stage build for Data Analysis Service

# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and fetch dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build the JAR (skip test compilation entirely)
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose service port
EXPOSE 6072

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:6072/actuator/health || exit 1

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
