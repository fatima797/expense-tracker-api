# Expense Tracker API

A Spring Boot REST API designed to help track personal expenses and manage budgets.

**Status:** Active development - authentication complete, create and get single expense endpoints complete.

[![CI](https://github.com/fatima797/expense-tracker-api/actions/workflows/ci.yml/badge.svg)](https://github.com/fatima797/expense-tracker-api/actions)

---

## Tech Stack

- Java 17
- Spring Boot 3.5.14
- Spring Security
- Spring Data JPA
- Maven Wrapper
- Lombok
- JJWT (JWT generation and validation)
- MySQL 8.0
- H2 (in-memory database for testing)
- Docker & Docker Compose

## Prerequisites

- Docker Desktop
- JDK 17+

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/fatima797/expense-tracker-api.git
cd expense-tracker-api
```

### 2. Run with Docker Compose

Copy `.env.example` to `.env` and fill in your values:

```bash
cp .env.example .env
```

```env
DB_URL=jdbc:mysql://mysql:3306/expense_tracker_db
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
DB_ROOT_PASSWORD=your_root_password_here
JWT_SECRET=your_secret_key_here
JWT_EXPIRATION=86400000
```

> [!NOTE]
> `DB_ROOT_PASSWORD` is required for initializing the MySQL container.

Run the application:

```bash
docker compose up --build
```

To stop:

```bash
docker compose down
```

The application will be available at:
http://localhost:8081

---

## Running Tests

Tests use an in-memory H2 database and require no additional setup.

```bash
./mvnw test
```

## API Endpoints

| Method | Endpoint                      | Description                    | Auth Required |
| ------ | ----------------------------- | ------------------------------ | ------------- |
| POST   | `/api/v1/auth/register`       | Register a new user            | No            |
| POST   | `/api/v1/auth/login`          | Authenticate and receive JWT   | No            |
| GET    | `/api/v1/users/me`            | Get authenticated user profile | Yes           |
| POST   | `/api/v1/expenses`            | Create a new expense           | Yes           |
| GET    | `/api/v1/expenses/{publicId}` | Get a single expense           | Yes           |

### POST `/api/v1/auth/register`

**Request Body:**

```json
{
  "name": "janedoe",
  "email": "jane@example.com",
  "password": "SecurePass123!"
}
```

**Response `201 Created`:**

```json
{
  "publicId": "f7c3b9d5-4a12-4c75-bc8f-5bbbdc87b44f",
  "email": "jane@example.com",
  "createdAt": "2026-02-05T10:42:13"
}
```

### POST `/api/v1/auth/login`

**Request Body:**

```json
{
  "email": "jane@example.com",
  "password": "SecurePass123!"
}
```

**Response `200 OK`:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "jane@example.com",
  "publicId": "f7c3b9d5-4a12-4c75-bc8f-5bbbdc87b44f"
}
```

### GET `/api/v1/users/me`

**Headers:**

```
Authorization: Bearer <token>
```

**Response `200 OK`:**

```json
{
  "name": "janedoe",
  "email": "jane@example.com",
  "publicId": "f7c3b9d5-4a12-4c75-bc8f-5bbbdc87b44f"
}
```

### POST `/api/v1/expenses`

Creates a new expense entry bound to the authenticated user.

**Validation rules:**

- `amount` — required, must be greater than 0, max 2 decimal places
- `category` — required, accepted values: `GROCERIES`, `LEISURE`, `UTILITIES`, `ELECTRONICS`, `CLOTHING`, `HEALTH`, `OTHERS`
- `date` — required, must not be in the future
- `description` — optional, but if provided must be between 1 and 255 characters

**Headers:**

```
Authorization: Bearer <token>
```

**Request Body:**

```json
{
  "description": "Bought eggs and milk",
  "amount": 25.95,
  "category": "GROCERIES",
  "date": "2026-05-31"
}
```

**Response `201 Created`:**

```json
{
  "publicId": "e502ffeb-3388-461c-8285-2496e40073b1",
  "description": "Bought eggs and milk",
  "amount": 25.95,
  "category": "GROCERIES",
  "date": "2026-05-31"
}
```

### GET `/api/v1/expenses/{publicId}`

Retrieves a single expense by its public ID. The expense must belong to the authenticated user.

**Headers:**

```
Authorization: Bearer <token>
```

**Response `200 OK`:**

```json
{
  "publicId": "1ec49c74-69c9-4ce0-89fa-31b3ae4651ce",
  "description": "Bought eggs and milk",
  "amount": 22.95,
  "category": "GROCERIES",
  "date": "2026-06-30"
}
```

### Error responses:

`400 Bad Request` - validation failure:

```json
{
  "status": 400,
  "errors": {
    "amount": "Amount must have at most 10 integer digits and 2 decimal places",
    "date": "Date must not be in the future"
  },
  "timestamp": "2026-05-31T17:14:38"
}
```

`400 Bad Request` - invalid category value:

```json
{
  "status": 400,
  "errors": {
    "category": "Invalid value 'TECH' for field 'category'. Accepted values are: GROCERIES, LEISURE, UTILITIES, ELECTRONICS, CLOTHING, HEALTH, OTHERS"
  },
  "timestamp": "2026-05-31T17:14:38"
}
```

`400 Bad Request` - invalid amount:

```json
{
  "status": 400,
  "errors": {
    "amount": "Amount must be greater than 0"
  },
  "timestamp": "2026-05-31T17:14:38"
}
```

`409 Conflict` - duplicate email on registration:

```json
{
  "status": 409,
  "errors": {
    "email": "Email already exists"
  },
  "timestamp": "2026-05-31T17:14:38"
}
```

All endpoints may also return:

`401 Unauthorized` - unauthorized access:

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication is required to access this resource"
}
```

`404 Not Found` - expense not found or belongs to another user:

```json
{
  "status": 404,
  "errors": {
    "expense": "Expense not found with id: 1ec49c74-69c9-4ce0-89fa-31b3ae4651ce"
  },
  "timestamp": "2026-07-11T18:20:45.077013768"
}
```

> **Note:** Authentication errors (`401`) return a different response structure from application errors (`400`, `409`).
> This is a known limitation and will be unified in a future release.

## Features

- **Single Expense Retrieval:** Fetch individual expense entries using unique public IDs (`UUID`), with enforced user ownership.
- **Secure Authentication:** JWT-based login with Spring Security
- **JWT Filter:** OncePerRequestFilter validates JWT on every secured request
- **Expense Management:** Authenticated users can create and persist expense entries
- **Input Validation:** Field-level validation with descriptive error messages including accepted enum values
- **Ownership Enforcement:** Expenses are scoped to the authenticated user via JWT claims
- **Error Handling:** Global exception handler returns structured, field-level error responses
- **Password Safety:** Passwords hashed using BCrypt
- **Persistence:** MySQL 8.0 with JPA/Hibernate
- **Containerization:** Fully Dockerized with Docker Compose
- **CI/CD:** GitHub Actions pipeline runs `./mvnw clean verify` on all pull requests targeting `main`

## Upcoming features

- Retrieve all expenses with pagination and date range filtering
- Update and delete expense entries
- Expense filtering by category and date range

## Project Challenge Source

This project is inspired by the **Expense Tracker API** challenge provided by [roadmap.sh](https://roadmap.sh)

[View the original challenge instructions here](https://roadmap.sh/projects/expense-tracker-api)
