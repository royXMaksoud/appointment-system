# Auth Service

## Overview
The Auth Service is the core authentication and authorization microservice for the Care Management System. It handles user authentication, JWT token generation, token refresh, and password management using secure hashing and token-based authentication.

## Service Details
- **Port**: 6061
- **Framework**: Spring Boot 3.2.5
- **Technology Stack**: Spring Cloud 2023.0.3, PostgreSQL
- **Language**: Java 17
- **Shared Library**: Uses Core Shared Library 0.0.1-SNAPSHOT

## Key Technologies & Dependencies
- **Spring Security 6.x**: Core authentication framework
- **JWT (JJWT 0.11.5)**: Token-based authentication
- **Spring Data JPA**: Database persistence
- **PostgreSQL**: Primary data store
- **OpenFeign**: Service-to-service communication
- **Netflix Eureka Client**: Service discovery
- **Resilience4j**: Fault tolerance patterns
  - Circuit Breaker
  - Retry
  - Rate Limiter
  - Bulkhead
  - Time Limiter
- **MapStruct 1.6.3**: Object mapping/transformation
- **Lombok 1.18.30**: Code generation utilities
- **Micrometer Tracing (Zipkin)**: Distributed tracing
- **SpringDoc OpenAPI (WebMVC UI 2.5.0)**: API documentation
- **Spring Cloud Config Client**: Centralized configuration

## Core Responsibilities
1. **User Authentication**: Validates credentials and authenticates users
2. **JWT Token Generation**: Creates JWT tokens with expiration
3. **Token Refresh**: Issues new tokens when refresh tokens are provided
4. **User Registration**: Creates new user accounts with password hashing
5. **Password Management**: Secure password storage using BCrypt
6. **Token Validation**: Validates JWT tokens for other services
7. **User Session Management**: Maintains user session state
8. **OAuth2 Integration**: May support OAuth2 flows

## Key Configuration Files
- `pom.xml`: Maven dependencies and build configuration
- `application.yml`: Spring Boot application configuration
- `application-docker.yml`: Docker environment configuration
- Database schema: PostgreSQL tables for users and tokens

## API Endpoints
```
POST /auth/login - Authenticate user and receive JWT token
POST /auth/register - Register new user
POST /auth/refresh-token - Refresh expired JWT token
POST /auth/validate-token - Validate JWT token
POST /auth/logout - Logout user
GET /auth/user-info - Get current user information
POST /auth/change-password - Change user password
```

## Important Notes
- Uses **BCrypt** for password hashing (never stores plaintext passwords)
- JWT tokens contain user claims including roles and permissions
- Tokens have **expiration times** - refresh tokens used to get new access tokens
- Service registers with **Eureka** for service discovery
- Uses **Resilience4j** patterns for fault tolerance when calling other services
- Core Shared Library provides JWT utilities and security context management

## Integration Points
- **Service Registry**: Registers with Eureka for discovery
- **Config Server**: Retrieves configuration from centralized config server
- **Access Management Service**: May validate permissions and roles
- **PostgreSQL Database**: Stores user credentials and token information
- **Zipkin**: Sends tracing data for distributed tracing
- **Other Services**: Provides token validation for inter-service communication

## Database Schema
- **users**: Stores user credentials, email, status
- **user_roles**: Maps users to roles
- **tokens**: Stores refresh tokens and their expiration
- **login_history**: Optional tracking of user login activities

## Development & Deployment
- **Build**: `mvn clean package`
- **Run**: `java -jar auth-service-0.0.1-SNAPSHOT.jar`
- **Docker**: Dockerfile available for containerization
- **Configuration**: Managed via Spring Cloud Config Server
- **Database Migration**: Uses Liquibase or manual schema creation

## Security Considerations
- **BCrypt Password Hashing**: Never stores plaintext passwords
- **JWT Expiration**: Access tokens expire after configured duration
- **Refresh Token Rotation**: Optional rotation for enhanced security
- **HTTPS Required**: Should always use HTTPS in production
- **Token Validation**: All tokens validated before processing
- **Spring Security Filters**: Protects endpoints with authentication checks

## Testing
- Unit tests for authentication logic
- Integration tests with PostgreSQL
- OAuth2/JWT token validation tests
