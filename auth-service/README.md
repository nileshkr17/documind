# Authentication Service

A JWT-based authentication microservice for Documind.

## Features

- User registration with email and username
- User login with JWT token generation
- Password encryption using BCrypt
- Role-based access control (RBAC)
- Token validation endpoint

## Technologies

- Spring Boot 3.5.5
- Spring Security
- JWT (JSON Web Tokens)
- PostgreSQL
- JPA/Hibernate

## Running the Service

### Prerequisites

- Java 17+
- PostgreSQL running on localhost:5432
- Database `ragbot` should exist

### Start the service

```bash
cd auth-service
mvn clean install
mvn spring-boot:run
```

The service will start on `http://localhost:8082`

## API Endpoints

### Register a new user

```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

### Login

```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

### Validate Token

```bash
GET /api/auth/validate
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```
Token is valid
```

## Configuration

Configuration is in `src/main/resources/application.properties`:

```properties
server.port=8082
spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/ragbot
spring.datasource.username=raguser
spring.datasource.password=ragpass
jwt.secret=<your-secret-key>
jwt.expiration=86400000
```

## Database Schema

The service creates the following tables:

### users
- id (BIGSERIAL PRIMARY KEY)
- username (VARCHAR UNIQUE NOT NULL)
- email (VARCHAR UNIQUE NOT NULL)
- password (VARCHAR NOT NULL)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

### user_roles
- user_id (BIGINT FOREIGN KEY)
- role (VARCHAR)

## Security

- Passwords are hashed using BCrypt
- JWT tokens expire after 24 hours (configurable)
- CORS is enabled for all origins (configure as needed for production)
- Stateless session management

## Integration with Main API

To integrate with the main Documind API (port 8080), add JWT validation filter to the main API that validates tokens issued by this auth service.
