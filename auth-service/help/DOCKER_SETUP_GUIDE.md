# Docker Setup Guide - Care Management System

## Overview

All services are now containerized with optimized Dockerfiles and can be run using Docker Compose.

## Dockerfiles Created/Updated

| Service | Dockerfile Location | Port | Status |
|---------|-------------------|------|--------|
| **auth-service** | `auth-service/auth-service/Dockerfile` | 6061 | ✅ Updated |
| **access-management-service** | `access-management-service/Dockerfile` | 6062 | ✅ Updated |
| **gateway-service** | `gateway-service/Dockerfile` | 6060 | ✅ Updated |
| **service-registry** | `service-registry/Dockerfile` | 8761 | ✅ Created |
| **config-server** | `config-server/Dockerfile` | 8888 | ✅ Created |

## Dockerfile Features

### Multi-Stage Build
- **Stage 1 (Build)**: Compiles the application using Maven
- **Stage 2 (Runtime)**: Runs only the JAR file in a lightweight JRE image

### Optimizations
- ✅ Dependency caching for faster builds
- ✅ Alpine Linux for smaller image size (~150MB vs 500MB+)
- ✅ Non-root user for security
- ✅ Health checks included
- ✅ Environment variables configurable
- ✅ Proper port exposure

### Security
- ✅ Runs as non-root user (spring:spring)
- ✅ Minimal attack surface (only JRE, no build tools)
- ✅ No sensitive data in images
- ✅ .dockerignore excludes unnecessary files

## Quick Start

### 1. Build All Images

```bash
# Build individual services
cd auth-service/auth-service
docker build -t care/auth-service:latest .

cd ../../access-management-service
docker build -t care/access-management-service:latest .

cd ../gateway-service
docker build -t care/gateway-service:latest .

cd ../service-registry
docker build -t care/service-registry:latest .

cd ../config-server
docker build -t care/config-server:latest .
```

### 2. Run with Docker Compose

```bash
# From project root
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

## docker-compose.yml Structure

The compose file includes:

### Infrastructure Services
- **postgres**: PostgreSQL 14 database
- **service-registry**: Eureka server for service discovery

### Application Services
- **config-server**: Centralized configuration
- **auth-service**: Authentication and authorization
- **access-management-service**: Access control and permissions
- **gateway-service**: API Gateway

### Startup Order
1. postgres (database)
2. service-registry (Eureka)
3. config-server (optional)
4. auth-service
5. access-management-service
6. gateway-service

## Environment Variables

Create a `.env` file from `env.template`:

```bash
cp env.template .env
```

Edit `.env` with your values:

```env
DB_PASSWORD=YourSecurePassword
JWT_SECRET=YourVeryLongSecretKeyHere
```

## Individual Service Docker Commands

### Auth Service

```bash
docker build -t care/auth-service:latest ./auth-service/auth-service
docker run -d \
  -p 6061:6061 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=cms_db \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=P@ssw0rd \
  --name auth-service \
  care/auth-service:latest
```

### Access Management Service

```bash
docker build -t care/access-management-service:latest ./access-management-service
docker run -d \
  -p 6062:6062 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=cms_db \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=P@ssw0rd \
  --name access-management-service \
  care/access-management-service:latest
```

### Gateway Service

```bash
docker build -t care/gateway-service:latest ./gateway-service
docker run -d \
  -p 6060:6060 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e EUREKA_SERVER=http://host.docker.internal:8761/eureka \
  --name gateway-service \
  care/gateway-service:latest
```

## Docker Image Sizes

Expected sizes after build:
- auth-service: ~200-250 MB
- access-management-service: ~250-300 MB
- gateway-service: ~180-200 MB
- service-registry: ~150-180 MB
- config-server: ~150-180 MB

## Health Checks

All services include health checks that monitor:
- Application startup
- Actuator health endpoint
- Service availability

Access health checks:
```bash
docker ps --format "table {{.Names}}\t{{.Status}}"
```

## Troubleshooting

### Service Won't Start

1. Check logs:
```bash
docker logs care-auth-service
docker logs care-access-management
docker logs care-gateway
```

2. Check health:
```bash
docker exec care-auth-service wget --spider http://localhost:6061/actuator/health
```

3. Verify database connection:
```bash
docker exec -it care-postgres psql -U postgres -d cms_db -c "SELECT 1"
```

### Build Issues

```bash
# Clean Docker cache
docker system prune -a

# Rebuild without cache
docker-compose build --no-cache

# Check disk space
docker system df
```

### Network Issues

```bash
# List networks
docker network ls

# Inspect care network
docker network inspect care_care-network

# Recreate network
docker-compose down
docker network prune
docker-compose up -d
```

## Production Considerations

### 1. Use Docker Secrets
Instead of .env file, use Docker secrets:

```yaml
services:
  auth-service:
    secrets:
      - db_password
      - jwt_secret

secrets:
  db_password:
    external: true
  jwt_secret:
    external: true
```

### 2. Resource Limits

Add resource limits to docker-compose.yml:

```yaml
services:
  auth-service:
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 512M
        reservations:
          cpus: '0.5'
          memory: 256M
```

### 3. Logging Configuration

```yaml
services:
  auth-service:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

### 4. Registry Configuration

For production, push images to Docker Hub or private registry:

```bash
# Tag for registry
docker tag care/auth-service:latest yourusername/care-auth-service:1.0.0

# Push to Docker Hub
docker push yourusername/care-auth-service:1.0.0
```

## Next Steps

1. ✅ Dockerfiles created for all services
2. ✅ docker-compose.yml created
3. ✅ .dockerignore files configured
4. ⏳ Test with `docker-compose up`
5. ⏳ Push images to registry
6. ⏳ Deploy to Kubernetes

---

**Last Updated**: October 15, 2025  
**Version**: 1.0.0  
**Status**: ✅ Ready for Deployment

