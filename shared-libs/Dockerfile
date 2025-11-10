# Dockerfile for Core Shared Library
# This builds the shared library and makes it available as a base image

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code
COPY src ./src

# Build and install to local Maven repository
RUN mvn clean install -DskipTests -B

# The JAR is now in /root/.m2/repository/com/sharedlib/core-shared-lib/0.0.1-SNAPSHOT/

