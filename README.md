<div align="center">

# 📚 Learning Management System (LMS)

**A production-ready REST API for managing courses, instructors, and students**

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://swagger.io/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

[Features](#-features) · [Architecture](#-architecture) · [API Reference](#-api-reference) · [Getting Started](#-getting-started) · [Security](#-security)

</div>

---

## 📖 Overview

LMS is a **fully secured RESTful API** built with Spring Boot 3, designed to manage the complete lifecycle of an online learning platform. It supports three user roles — **Admin**, **Instructor**, and **Student** — each with fine-grained access control enforced at both the route and method level.

The system handles:
- 🔐 JWT-based stateless authentication
- 👥 Role-based access control (RBAC)
- 📋 Course creation, update, and deletion
- 🎓 Student enrollment management
- 🛡️ Rate limiting on authentication endpoints

---

## ✨ Features

| Feature | Details |
|---|---|
| **JWT Authentication** | Stateless, HS256-signed tokens with configurable expiry |
| **Role-Based Access** | Three roles: `ADMIN`, `INSTRUCTOR`, `STUDENT` |
| **Rate Limiting** | Bucket4j — protects login & signup from brute-force |
| **Swagger UI** | Full API documentation with Bearer token support |
| **Global Error Handling** | Consistent JSON error responses across all exceptions |
| **Password Encoding** | DelegatingPasswordEncoder (bcrypt by default) |
| **CORS Support** | Configurable allowed origins |
| **Validation** | Bean Validation on all request DTOs |

---

## 🏗️ Architecture

The project follows a clean **Layered Architecture**:

```
src/main/java/Ebrahem/Group/LMS/
│
├── Configuration/          # Security, JWT filter, CORS, Swagger
│   ├── AppConfig.java
│   ├── CorsConfig.java
│   ├── JwtAuthenticationFilter.java
│   └── SwaggerConfig.java
│
├── Controller/             # REST endpoints (HTTP layer)
│   ├── AuthController.java
│   ├── AdminController.java
│   ├── InstructorController.java
│   ├── StudentController.java
│   └── EnrollmentController.java
│
├── Service/                # Business logic interfaces + implementations
│   ├── AuthService.java / Impl/
│   ├── AdminService.java / Impl/
│   ├── InstructorFunctionalityService.java / Impl/
│   ├── StudentService.java / Impl/
│   ├── EnrollmentService.java / Impl/
│   └── JwtProviderService.java / Impl/
│
├── Model/
│   ├── Entity/             # JPA entities (User, Course, Enrollment)
│   ├── Dtos/               # Request & Response records
│   └── Enums/              # Role, Status
│
├── Repositories/           # Spring Data JPA interfaces
├── Security/               # UserDetails implementation
└── Exception/              # Custom exceptions & global handler
```

### Entity Relationship

```
User (1) ──────── (N) Course          [Instructor creates courses]
User (1) ──────── (N) Enrollment      [Student enrolls in courses]
Course (1) ─────── (N) Enrollment     [Course has many enrollments]
```

---

## 🔐 Security

### Authentication Flow

```
Client                          Server
  │                               │
  ├──── POST /api/v1/auth/login ──►│
  │     { email, password }        │ 1. Validate credentials
  │                                │ 2. Generate JWT (24h)
  │◄─── { token, role, userId } ───│
  │                                │
  ├──── GET /api/v1/student/... ──►│
  │     Authorization: Bearer jwt  │ 3. Extract & validate token
  │                                │ 4. Set SecurityContext
  │◄─── { data } ─────────────────│
```

### Role Permissions

| Endpoint Pattern | ADMIN | INSTRUCTOR | STUDENT |
|---|:---:|:---:|:---:|
| `POST /api/v1/auth/**` | ✅ | ✅ | ✅ |
| `GET /api/v1/student/**` | ✅ | ✅ | ✅ |
| `POST /api/v1/instructor/**` | ❌ | ✅ | ❌ |
| `DELETE /api/v1/instructor/**` | ❌ | ✅ | ❌ |
| `GET /api/v1/admin/**` | ✅ | ❌ | ❌ |
| `POST /api/v1/enrollment` | ❌ | ✅ | ✅ |

### JWT Configuration

```yaml
# application.yml
jwt:
  secret: your-256-bit-base64-encoded-secret-key
```

> ⚠️ **Never commit your JWT secret to version control.** Use environment variables in production.

---

## 📡 API Reference

### Authentication — `/api/v1/auth`

#### Register
```http
POST /api/v1/auth/sign
Content-Type: application/json

{
  "userEmail": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "STUDENT",
  "password": "StrongPass1"
}
```

**Response `201 Created`:**
```json
{
  "logInId": "uuid",
  "fallName": "john doe",
  "logInEmail": "john@example.com",
  "role": "STUDENT",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": null
}
```

#### Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "logInEmail": "john@example.com",
  "logInPassword": "StrongPass1"
}
```

#### Reset Password
```http
POST /api/v1/auth/resetPassword/{newPassword}/{userEmail}
```

---

### Instructor — `/api/v1/instructor` 🔒 `INSTRUCTOR only`

#### Create Course
```http
POST /api/v1/instructor/createCourse
Authorization: Bearer {token}
Content-Type: application/json

{
  "courseName": "Spring Boot Mastery",
  "instructorName": "john doe",
  "courseDuration": "2024-12-31T00:00:00"
}
```

**Response `201 Created`:**
```json
{
  "instructorId": "uuid",
  "courseId": "uuid",
  "courseTitle": "spring boot mastery",
  "instructorName": "john doe",
  "createdAt": "2025-04-25T10:00:00",
  "updatedAt": "2025-04-25T10:00:00",
  "courseDuration": "2024-12-31T00:00:00"
}
```

#### Update Course
```http
PUT /api/v1/instructor/updateCourse/{courseId}
Authorization: Bearer {token}
```

#### Delete Course
```http
DELETE /api/v1/instructor/deleteCourse/{courseId}
Authorization: Bearer {token}
```

---

### Student — `/api/v1/student` 🔒 `STUDENT | INSTRUCTOR | ADMIN`

#### Get All Courses
```http
GET /api/v1/student/allCourses
Authorization: Bearer {token}
```

---

### Enrollment — `/api/v1/enrollment` 🔒 `STUDENT | INSTRUCTOR`

#### Enroll in Course
```http
POST /api/v1/enrollment
Authorization: Bearer {token}
Content-Type: application/json

{
  "courseName": "spring boot mastery",
  "studentName": "jane doe",
  "studentId": "uuid",
  "courseID": "uuid"
}
```

---

### Admin — `/api/v1/admin` 🔒 `ADMIN only`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/admin/Users` | Get all students & instructors |
| `GET` | `/admin/allUsers` | Get all users with tokens |
| `DELETE` | `/admin/{id}` | Delete a user by ID |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### Installation

**1. Clone the repository**
```bash
git clone https://github.com/EbrahemGroup/lms-api.git
cd lms-api
```

**2. Configure the database**
```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/lms_db
    username: your_db_user
    password: your_db_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

jwt:
  secret: your-base64-encoded-256-bit-secret
```

**3. Run the application**
```bash
mvn spring-boot:run
```

**4. Access Swagger UI**
```
http://localhost:8080/swagger-ui/index.html
```

### Quick Test with cURL

```bash
# 1. Register
curl -X POST http://localhost:8080/api/v1/auth/sign \
  -H "Content-Type: application/json" \
  -d '{"userEmail":"admin@lms.com","firstName":"Admin","lastName":"User","role":"ADMIN","password":"Admin1234"}'

# 2. Login and grab token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"logInEmail":"admin@lms.com","logInPassword":"Admin1234"}' | jq -r .token)

# 3. Get all users (ADMIN only)
curl http://localhost:8080/api/v1/admin/allUsers \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security 6 + JWT (jjwt) |
| Data | Spring Data JPA + Hibernate |
| Database | PostgreSQL |
| Rate Limiting | Bucket4j |
| Validation | Jakarta Bean Validation |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |
| Utilities | Lombok |

---

## 📁 Data Models

### User
| Field | Type | Constraints |
|---|---|---|
| `userId` | UUID | Primary Key, auto-generated |
| `userName` | String | Not null |
| `userEmail` | String | Unique, not null |
| `userPassword` | String | Encoded, not null |
| `role` | Enum | `ADMIN` / `INSTRUCTOR` / `STUDENT` |
| `createdAt` | LocalDateTime | Auto-set on persist |

### Course
| Field | Type | Constraints |
|---|---|---|
| `courseId` | UUID | Primary Key |
| `courseTitle` | String | Not null, stored lowercase |
| `courseDuration` | LocalDateTime | Nullable |
| `instructor` | User (FK) | ManyToOne |
| `createdAt` | LocalDateTime | Auto-set |
| `updatedAt` | LocalDateTime | Auto-updated |

### Enrollment
| Field | Type | Constraints |
|---|---|---|
| `enrollmentId` | UUID | Primary Key |
| `user` | User (FK) | ManyToOne |
| `course` | Course (FK) | ManyToOne |
| `status` | Enum | `ACTIVE` / `COMPLETED` / `DROPPED` |
| `progress` | float | Default: 0.0 |
| `enrollAt` | LocalDateTime | Auto-set |

---

## ⚠️ Error Responses

All errors return a consistent JSON structure:

```json
{
  "message": "You are not authorized to access this resource.",
  "status": 403,
  "path": "/api/v1/admin/Users",
  "timestamp": "2025-04-25T10:30:00"
}
```

| Status | Scenario |
|---|---|
| `400` | Validation errors (field-level messages) |
| `401` | Missing or invalid JWT token |
| `403` | Insufficient role permissions |
| `404` | Resource not found |
| `406` | Duplicate entity (e.g. user already enrolled) |
| `429` | Too many requests (rate limit exceeded) |
| `500` | Unexpected server error |

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Built with ❤️ by **Ebrahem Group**

⭐ Star this repo if you found it helpful!

</div>
