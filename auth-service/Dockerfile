# ===========================
# Stage 1: Build shared library
# ===========================
FROM maven:3.9.6-eclipse-temurin-17 AS build-shared-lib
WORKDIR /build

# Copy shared-libs from parent directory (since context is auth-service)
COPY ../shared-libs /build/shared-libs

# Build shared-libs
WORKDIR /build/shared-libs
RUN mvn clean install -DskipTests -B


# ===========================
# Stage 2: Build Auth Service
# ===========================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven cache
COPY --from=build-shared-lib /root/.m2 /root/.m2

# Copy auth-service module files
COPY pom.xml .
COPY src ./src

# Build jar
RUN mvn clean package -Dmaven.test.skip=true -B



# ===========================
# Stage 3: Runtime
# ===========================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache wget
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SERVER_PORT=6061 \
    SPRING_PROFILES_ACTIVE=docker

EXPOSE 6061

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:6061/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
