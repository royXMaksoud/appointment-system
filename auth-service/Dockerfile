# Multi-stage build for Auth Service with core-shared-lib

# Stage 1: Build shared library first
FROM maven:3.9.6-eclipse-temurin-17 AS build-shared-lib
WORKDIR /shared-lib

# Copy shared lib (from root context)
COPY shared-libs/core-shared-lib/core-shared-lib/pom.xml ./pom.xml
COPY shared-libs/core-shared-lib/core-shared-lib/src ./src

# Build and install shared lib to local Maven repository
RUN mvn clean install -DskipTests -B

# Stage 2: Build auth service
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven repository from shared-lib stage
COPY --from=build-shared-lib /root/.m2 /root/.m2

# Copy pom.xml and download dependencies
COPY auth-service/auth-service/pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build
COPY auth-service/auth-service/src ./src
RUN mvn clean package -DskipTests -B

# Stage 3: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SERVER_PORT=6061 \
    SPRING_PROFILES_ACTIVE=docker

# Expose port
EXPOSE 6061

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:6061/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
