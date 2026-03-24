# EzyEnglish Authentication Service

Spring Boot REST API with MongoDB and JWT authentication for user management with role-based access control.

## Features

- User registration and authentication
- JWT token-based security
- Role-based access control (STUDENT, TEACHER, ADMIN)
- MongoDB integration
- Password encryption with BCrypt
- Email and username validation

## Technologies

- Java 17
- Spring Boot 3.2.3
- Spring Security
- Spring Data MongoDB
- JWT (JSON Web Tokens)
- Lombok
- Maven

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB Atlas account (connection string provided)

## Configuration

The application is configured to run on port 8082 with MongoDB Atlas connection.

Configuration file: `src/main/resources/application.properties`

## API Endpoints

### Signup
**POST** `/api/auth/signup`

Request body:
```json
{
  "username": "alice01",
  "email": "alice@example.com",
  "password": "Pa$$w0rd",
  "firstName": "Alice",
  "lastName": "Jones",
  "phone": "+1234567890",
  "roles": ["student"]
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": "507f1f77bcf86cd799439011",
  "username": "alice01",
  "email": "alice@example.com",
  "roles": ["STUDENT"]
}
```

### Signin
**POST** `/api/auth/signin`

Request body:
```json
{
  "username": "dasunhansajith@gmail.com",
  "password": "123456@a"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": "507f1f77bcf86cd799439011",
  "username": "dasunhansajith@gmail.com",
  "email": "dasunhansajith@gmail.com",
  "roles": ["STUDENT"]
}
```

## Roles

- `student` - Default role for regular users
- `teacher` - Role for instructors
- `admin` - Administrative role with full access

## Running the Application

1. Clone the repository
2. Navigate to project directory
3. Run the application:
```bash
mvn spring-boot:run
```

Or build and run the JAR:
```bash
mvn clean package
java -jar target/auth-1.0.0.jar
```

The server will start on `http://localhost:8082`

## Testing the API

Using curl:

```bash
# Signup
curl -X POST http://localhost:8082/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User",
    "phone": "+1234567890",
    "roles": ["student"]
  }'

# Signin
curl -X POST http://localhost:8082/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

## Security

- Passwords are encrypted using BCrypt
- JWT tokens expire after 24 hours (configurable)
- Stateless authentication
- CORS enabled for all origins (configure for production)

## Project Structure

```
src/main/java/com/ezyenglish/auth/
├── config/          # Security configuration
├── controller/      # REST controllers
├── dto/            # Request/Response objects
├── model/          # Entity classes
├── repository/     # MongoDB repositories
├── security/       # JWT and security components
└── service/        # Business logic
```