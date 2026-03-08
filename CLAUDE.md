# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.8 backend application (Java 17) implementing a user management system with JWT authentication, Redis session storage, and MySQL database. The project uses MyBatis Plus for data access, MapStruct for object mapping, Lombok for boilerplate reduction, and Swagger for API documentation.

## Common Development Tasks

### Building and Running

- **Build the project**: `./mvnw clean install`
- **Run the application**: `./mvnw spring-boot:run`
- **Run tests**: `./mvnw test`
- **Run a single test class**: `./mvnw test -Dtest=ClassName`
- **Compile without tests**: `./mvnw compile`
- **Package as JAR**: `./mvnw package`

### Development Utilities

- **Hot reload**: DevTools is configured (`spring-boot-devtools`). Changes in `src/main/java` trigger automatic restart.
- **Database console**: MyBatis Plus SQL logging is enabled (`StdOutImpl`). Check console for executed SQL.
- **API documentation**: Swagger UI is available at `http://localhost:8080/swagger-ui.html` when the application is running.

## Architecture Overview

### Key Technologies

- **Spring Boot 3.5.8** with Java 17
- **MyBatis Plus** for database operations (ORM)
- **MySQL** database (configured in `application.properties`)
- **Redis** for token storage and session management
- **JWT (jjwt)** for stateless authentication
- **MapStruct** for DTO/entity conversion
- **Lombok** for getters/setters/constructors
- **Spring AOP** for permission checking (aspect defined but not fully implemented)
- **SpringDoc OpenAPI** for Swagger documentation
- **Druid** for database connection pooling
- **Dotenv Java** for environment variables (not currently used)

### Project Structure

```
src/main/java/com/example/demo/
├── common/
│   ├── annotation/      # Custom annotations (e.g., @RequiresPermissions)
│   ├── aspect/          # AOP aspects (PermissionAspect)
│   ├── context/         # ThreadLocal user context (UserContext)
│   ├── exception/       # BusinessException and GlobalExceptionHandler
│   └── result/          # Unified API response (Result, ResultCode)
├── config/              # Configuration classes (WebConfig, RedisConfig)
├── controller/          # REST controllers (AuthController, UserController)
├── handler/             # MyBatis Plus MetaObjectHandler (auto-fill timestamps)
├── interceptor/         # JwtInterceptor for authentication
├── mapper/              # MyBatis Plus mappers (extends BaseMapper)
├── model/
│   ├── converter/       # MapStruct converters (UserConverter)
│   ├── dto/             # Data Transfer Objects (LoginDTO, RegisterDTO, UserInfoDTO)
│   ├── entity/          # JPA-like entities (User) with MyBatis Plus annotations
│   └── vo/              # View Objects (UserInfoVO)
├── service/
│   ├── impl/            # Service implementations
│   └── *.java           # Service interfaces
└── utils/               # Utility classes (JwtUtils, RedisUtils, PasswordUtils)
```

### Authentication & Authorization Flow

1. **Registration/Login**: `AuthController` endpoints (`/auth/register`, `/auth/login`) create users and issue JWT tokens.
2. **Token Storage**: Issued tokens are stored in Redis with key `login:token:{userId}` and 30-minute TTL.
3. **Request Interception**: `JwtInterceptor` intercepts all requests (except excluded paths) and:
   - Extracts `Authorization: Bearer <token>` header
   - Validates JWT signature and expiration
   - Verifies token exists in Redis (prevents token revocation issues)
   - Performs sliding expiration (resets Redis TTL on each request)
   - Stores user ID in `UserContext` (ThreadLocal)
4. **Logout**: `AuthController.logout()` removes token from Redis.
5. **Permission Checking**: The `@RequiresPermissions` annotation and `PermissionAspect` are defined but not fully implemented (commented out). Permissions would be stored in Redis under `auth:perms:{userId}`.

### Database Layer

- **Entities**: Use MyBatis Plus annotations (`@TableName`, `@TableId`, `@TableField`)
- **Mappers**: Extend `BaseMapper<T>` for CRUD operations; custom SQL can be added via annotations or XML
- **Auto-fill**: `TimeMetaObjectHandler` automatically sets `createdAt` and `updatedAt` timestamps on insert/update
- **Connection Pool**: Druid configured via `spring.datasource.type`

### API Response Pattern

All REST endpoints return `Result<T>` wrapper with fields:
- `code`: Integer status code (200 for success, see `ResultCode` enum for others)
- `message`: Human-readable message
- `data`: Response payload (nullable)
- `timestamp`: LocalDateTime of response

Exceptions are handled by `GlobalExceptionHandler`:
- `BusinessException`: Returns custom code/message
- Validation errors: Returns concatenated error messages
- Other exceptions: Returns generic 500 error

### Configuration Files

- `application.properties`: Main configuration (database, devtools, MyBatis Plus logging)
- **Note**: Database credentials are hardcoded; consider using environment variables for production.
- JWT secret and expiration have defaults but can be overridden via `jwt.secret` and `jwt.expiration` properties.

## Important Notes for Development

1. **ThreadLocal Cleanup**: `UserContext.remove()` is called in `JwtInterceptor.afterCompletion()` to prevent memory leaks. Ensure any new interceptors or filters that use ThreadLocal follow this pattern.

2. **Password Security**: `PasswordUtils` uses SHA-256 + Base64 encoding. For production, consider using BCrypt or Argon2.

3. **Redis Dependency**: The application requires a Redis instance running (default localhost:6379). Authentication flow will fail if Redis is unavailable.

4. **Permission System**: The permission checking infrastructure (annotation, aspect, Redis storage) is partially implemented but disabled. To enable, uncomment relevant code in `JwtInterceptor` and `AuthServiceImpl`.

5. **CORS Configuration**: `WebConfig` allows origins `http://localhost:*` and `https://localhost:*` with credentials. Adjust for production frontend domains.

6. **MyBatis Plus Logging**: SQL statements are logged to console via `StdOutImpl`. Disable in production by removing the `log-impl` property.

7. **MapStruct + Lombok**: The Maven compiler plugin is configured with correct annotation processor order (`lombok-mapstruct-binding`). Do not change the order.

8. **Swagger Access**: Swagger UI paths are excluded from JWT interception. API docs are publicly accessible when the app is running.