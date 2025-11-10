# Docker Build Context Fix - Shared Library Solution

## Problem

Docker COPY commands cannot access files outside the build context. When using:

```yaml
auth-service:
  build:
    context: ./auth-service/auth-service  # Limited context
```

The Dockerfile cannot access `../../shared-libs/` because it's outside the context.

## Solution

### Changed docker-compose.yml Build Context

**Before**:
```yaml
auth-service:
  build:
    context: ./auth-service/auth-service  # ❌ Limited context
    dockerfile: Dockerfile
```

**After**:
```yaml
auth-service:
  build:
    context: .  # ✅ Root directory as context
    dockerfile: ./auth-service/auth-service/Dockerfile
```

### Updated Dockerfile Paths

**Before** (relative to service directory):
```dockerfile
COPY ../../shared-libs/core-shared-lib/core-shared-lib/pom.xml ./pom.xml  # ❌
COPY pom.xml .  # Service pom
COPY src ./src  # Service src
```

**After** (relative to root directory):
```dockerfile
# Stage 1: Build shared library
COPY shared-libs/core-shared-lib/core-shared-lib/pom.xml ./pom.xml  # ✅
COPY shared-libs/core-shared-lib/core-shared-lib/src ./src           # ✅

# Stage 2: Build service
COPY auth-service/auth-service/pom.xml .  # ✅
COPY auth-service/auth-service/src ./src  # ✅
```

## Files Modified

1. **docker-compose.yml**
   - auth-service: context changed to `.`
   - access-management-service: context changed to `.`

2. **auth-service/auth-service/Dockerfile**
   - Updated all COPY paths to be relative to root

3. **access-management-service/Dockerfile**
   - Updated all COPY paths to be relative to root

## How It Works Now

### Build Context Hierarchy

```
C:\Java\care\Code\  (ROOT - docker-compose build context)
├── shared-libs/
│   └── core-shared-lib/
│       └── core-shared-lib/
│           ├── pom.xml        ← Accessible from auth Dockerfile
│           └── src/           ← Accessible from auth Dockerfile
│
├── auth-service/
│   └── auth-service/
│       ├── Dockerfile         ← Executed from root context
│       ├── pom.xml
│       └── src/
│
└── access-management-service/
    ├── Dockerfile
    ├── pom.xml
    └── src/
```

### Dockerfile Multi-Stage Build

```dockerfile
# Stage 1: Build shared library
FROM maven:3.9.6-eclipse-temurin-17 AS build-shared-lib
WORKDIR /shared-lib
COPY shared-libs/core-shared-lib/core-shared-lib/pom.xml ./pom.xml
COPY shared-libs/core-shared-lib/core-shared-lib/src ./src
RUN mvn clean install -DskipTests -B

# Stage 2: Build service (with shared lib in .m2)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY --from=build-shared-lib /root/.m2 /root/.m2
COPY auth-service/auth-service/pom.xml .
RUN mvn dependency:go-offline -B || true
COPY auth-service/auth-service/src ./src
RUN mvn clean package -DskipTests -B

# Stage 3: Runtime
FROM eclipse-temurin:17-jre-alpine
# ... runtime configuration
```

## Building Services

### Using docker-compose (Recommended)

```bash
# From project root
docker-compose build

# Or build specific service
docker-compose build auth-service
docker-compose build access-management-service
```

### Using Docker CLI

```bash
# From project root (context = current directory)
docker build -f ./auth-service/auth-service/Dockerfile -t care/auth-service:latest .
docker build -f ./access-management-service/Dockerfile -t care/access-management:latest .
```

**Important**: Must run from `C:\Java\care\Code\` directory

## Services Affected

| Service | Build Context Changed | Dockerfile Paths Updated |
|---------|----------------------|-------------------------|
| auth-service | ✅ Yes | ✅ Yes |
| access-management-service | ✅ Yes | ✅ Yes |
| gateway-service | ❌ No (no shared-lib dependency) | ❌ No |
| service-registry | ❌ No | ❌ No |
| config-server | ❌ No | ❌ No |
| reference-data-service | ❌ No | ❌ No |

## Advantages

✅ **Single Build Context**: All services use root directory  
✅ **Access to Shared Libraries**: Can COPY from anywhere in project  
✅ **Consistent Approach**: Same pattern for all services  
✅ **No Manual Steps**: shared-lib built automatically  

## Verification

```bash
# Build should now succeed
cd C:\Java\care\Code
docker-compose build auth-service

# Expected output:
# [+] Building 120s
# => [build-shared-lib] building shared library
# => [build] building auth service
# => [runtime] creating final image
# Successfully built...
```

## Troubleshooting

### If Build Still Fails

1. **Verify you're in project root**:
```bash
cd C:\Java\care\Code
pwd  # Should show C:\Java\care\Code
```

2. **Check file exists**:
```bash
Test-Path shared-libs/core-shared-lib/core-shared-lib/pom.xml
# Should return True
```

3. **Clean Docker cache**:
```bash
docker system prune -a
docker-compose build --no-cache
```

4. **Check Dockerfile paths**:
All COPY commands should be relative to `C:\Java\care\Code\`

---

**Last Updated**: October 15, 2025  
**Status**: ✅ Fixed

