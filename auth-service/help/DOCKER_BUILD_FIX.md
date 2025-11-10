# Docker Build Fix - Shared Library Solution

## Problem

When building Docker images for auth-service and access-management-service, Maven fails with:

```
Could not find artifact com.sharedlib:core-shared-lib:jar:0.0.1-SNAPSHOT
```

## Root Cause

Both services depend on `core-shared-lib` which is a local library (not published to Maven Central or any external repository). During Docker build, this library is not available in the Maven repository.

## Solution Implemented

### Multi-Stage Dockerfiles with Shared Library Build

Modified Dockerfiles to build `core-shared-lib` first, then use it for dependent services.

#### For auth-service and access-management-service:

**Stage 1**: Build shared library
```dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS build-shared-lib
WORKDIR /shared-lib
COPY ../../shared-libs/core-shared-lib/core-shared-lib/pom.xml ./pom.xml
COPY ../../shared-libs/core-shared-lib/core-shared-lib/src ./src
RUN mvn clean install -DskipTests -B
```

**Stage 2**: Build service (with shared lib available)
```dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY --from=build-shared-lib /root/.m2 /root/.m2
COPY pom.xml .
RUN mvn dependency:go-offline -B || true
COPY src ./src
RUN mvn clean package -DskipTests -B
```

**Stage 3**: Runtime
```dockerfile
FROM eclipse-temurin:17-jre-alpine
# ... runtime configuration
```

## Files Modified

1. **auth-service/auth-service/Dockerfile** - Added shared-lib build stage
2. **access-management-service/Dockerfile** - Added shared-lib build stage
3. **shared-libs/core-shared-lib/core-shared-lib/Dockerfile** - Created
4. **shared-libs/core-shared-lib/core-shared-lib/.dockerignore** - Created

## Docker Build Context

When building from docker-compose, the build context must include the shared library:

```yaml
services:
  auth-service:
    build:
      context: ./auth-service/auth-service
      dockerfile: Dockerfile
```

The Dockerfile uses relative paths (`../../shared-libs/...`) to access the shared library from within the build context.

## Testing the Build

### Build Individual Service

```bash
# From project root
docker build -t care/auth-service:latest ./auth-service/auth-service
```

### Build with Docker Compose

```bash
docker-compose build auth-service
docker-compose build access-management-service
```

### Build All Services

```bash
docker-compose build
```

## Alternative Solution: Local Maven Repository

If the multi-stage approach doesn't work due to path issues, use this alternative:

### Option 1: Build shared-lib locally first

```bash
cd shared-libs/core-shared-lib/core-shared-lib
mvn clean install

# Then build Docker images (they'll use your local .m2)
docker-compose build
```

### Option 2: Use Maven wrapper in Dockerfile

```dockerfile
# Copy local .m2 repository
COPY --from=build-local /root/.m2 /root/.m2
```

### Option 3: Publish to Private Maven Repository

1. Set up Nexus or Artifactory
2. Publish core-shared-lib to it
3. Update pom.xml to use that repository
4. Remove custom build stages

## Verification

After build succeeds, verify:

```bash
# Check image size
docker images | grep care

# Run container
docker run -d --name test-auth care/auth-service:latest

# Check logs
docker logs test-auth

# Cleanup
docker stop test-auth
docker rm test-auth
```

---

**Last Updated**: October 15, 2025  
**Status**: ✅ Fixed

