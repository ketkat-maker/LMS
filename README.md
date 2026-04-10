# LMS (Learning Management System)


---  
## Overview
- Backend: Spring Boot REST API (package root: `Ebrahem.Group.LMS`) — provides auth (JWT), courses, enrollment, admin operations.



---

## Repo layout (important paths)
- Backend source: `src/main/java/...` (Spring Boot project)
- Primary frontend: `umbra-academy-main/`
- Alternate frontend: `src/frontend/`

---

## Requirements
- Java 17+ (or as configured in backend)
- Maven to run backend

---

## Backend — run (example)
From repo root (where `pom.xml` or `build.gradle` exists):

With Maven:
Check logs for server startup and port (default `8080`).

---

## Auth & JWT (expected behavior)
- Endpoints:
  - `POST /api/v1/auth/sign` — signup
  - `POST /api/v1/auth/login` — login
- Successful login response must include a token field (one of): `token`, `accessToken`, `jwt`.
- JWT payload should contain at least:
  - `sub` (user identifier, often email)
  - `exp` (expiry)
  - role info in `roles` / `authorities` / `role` / `scope`
- On 401 responses frontend should clear stored token and redirect to login.

---

## Main API endpoints 
All endpoints are relative to `/api/v1`:

- Auth
  - POST `/auth/sign` — register
  - POST `/auth/login` — login (returns JWT)

- Student
  - GET `/student/allCourses` — list available courses

- Instructor
  - POST `/instructor/createCourse` — create course
  - PUT `/instructor/updateCourse/:id` — update course
  - DELETE `/instructor/deleteCourse/:id` — delete course

- Enrollment
  - POST `/enrollment` — enroll student in course

- Admin
  - GET `/admin/Users` — list users (summary)
  - GET `/admin/allUsers` — list users (detailed)
  - DELETE `/admin/:id` — delete user

Refer to backend DTOs in `src/main/java/.../Model/Dtos` for exact request/response shapes.

---

## Troubleshooting (common issues)
- 404 on `POST /api/v1/auth/login`:


## Useful commands
- List server ports:
