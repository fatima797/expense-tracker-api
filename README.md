# Expense Tracker API

A Spring Boot REST API designed to help track personal expenses and manage budgets.

**Status:** Authentication & Security complete. Transitioning to Core Expense Management.

---

## Tech Stack

- Java 17
- Spring Boot 3.5.14
- Spring Security
- Spring Data JPA
- Maven Wrapper
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

| Method | Endpoint                | Description                    | Auth Required |
| ------ | ----------------------- | ------------------------------ | ------------- |
| POST   | `/api/v1/auth/register` | Register a new user            | No            |
| POST   | `/api/v1/auth/login`    | Authenticate and receive JWT   | No            |
| GET    | `/api/v1/users/me`      | Get authenticated user's email | Yes           |

### POST `/api/v1/auth/register`

**Request Body:**

```json
{
  "name": "janedoe",
  "email": "jane@example.com",
  "password": "SecurePass123!"
}
```

**Response Body:**

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

**Response Body:**

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

**Response Body:**

```json
{
  "name": "janedoe",
  "email": "jane@example.com",
  "publicId": "f7c3b9d5-4a12-4c75-bc8f-5bbbdc87b44f"
}
```

## Features

- **Secure Authentication:** JWT-based login with Spring Security
- **JWT Filter:** OncePerRequestFilter validates JWT on every secured request
- **Password Safety:** Passwords hashed using BCrypt
- **Persistence:** MySQL 8.0 with JPA/Hibernate
- **Containerization:** Fully Dockerized with Docker Compose

## Upcoming features

- Expense CRUD operations with secured endpoints

## Project Challenge Source

This project is inspired by the **Expense Tracker API** challenge provided by [roadmap.sh](https://roadmap.sh)

[View the original challenge instructions here](https://roadmap.sh/projects/expense-tracker-api)
