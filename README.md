# Expense Tracker API

A Spring Boot REST API designed to help track personal expenses and manage budgets.

**Status:** Under active development (Authentication & Security Phase).

---

## Tech Stack

- Java 17
- Spring Boot 3.4.0
- Spring Security
- Spring Data JPA
- Maven Wrapper
- MySQL 8.0
- H2 (in-memory database for testing)
- Docker & Docker Compose

## Prerequisites

### For Docker

- Docker Desktop

### For Local Development

- JDK 17+
- MySQL 8.0

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/fatima797/expense-tracker-api.git
cd expense-tracker-api
```

### 2. Choose Run Options

This project supports two execution modes:

- Docker Compose
- Local development using the Maven wrapper

---

### Option 1: Docker Compose

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

### Option 2: Local Development (Maven Wrapper)

The application uses a `local` Spring profile for running outside Docker.

Example configuration (`application-local.yml`):

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/expense_tracker_db
    username: username
    password: yourpassword

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8081
```

Run the application:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

> [!NOTE]
> When running locally, ensure MySQL is running and matches the `application-local.yml` configuration.

---

## Running Tests

Tests run using the `test` Spring profile.

```bash
./mvnw test
```

## API Endpoints

| Method | Endpoint                | Description                  | Auth Required |
| ------ | ----------------------- | ---------------------------- | ------------- |
| POST   | `/api/v1/auth/register` | Register a new user          | No            |
| POST   | `/api/v1/auth/login`    | Authenticate and receive JWT | No            |

### POST `/api/v1/auth/register`

**Request Body:**

```json
{
  "username": "janedoe",
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
  "username": "jane@example.com",
  "publicId": "ffbea372-8c03-4b62-8e0d-a0c835d85af1"
}
```

## Features

- **Secure Authentication:** JWT-based login with Spring Security
- **Password Safety:** Passwords hashed using BCrypt
- **Persistence:** MySQL 8.0 with JPA/Hibernate
- **Containerization:** Fully Dockerized with Docker Compose

## Roadmap

Upcoming features:

- Implement JWT filter for security protected endpoints.
- Secure endpoints for Expense CRUD operations.

## Project Challenge Source

This project is inspired by the **Expense Tracker API** challenge provided by roadmap.sh.

[View the original challenge instructions here](https://roadmap.sh/projects/expense-tracker-api)
