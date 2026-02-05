# Expense Tracker API

A Spring Boot REST API designed to help track personal expenses and manage budgets.

**Status:** Under active development (Authentication & Security Phase).

## Tech Stack
- Java 17
- Spring Boot 3.5.x
- Spring Security
- Spring Data JPA
- Maven Wrapper
- H2 Database
- Docker Compose

## Prerequisites
Before running the application, ensure you have the following installed:
- JDK 17+
- Docker (optional, only if running via Option 2)

*Note: You do not need Maven installed on your system; this project uses the included Maven Wrapper (`mvnw`).*

## Running the Application

### Option 1: Using the Maven Wrapper

1. Clone the repository
```bash
git clone https://github.com/fatima797/expense-tracker-api.git
cd expense-tracker-api
```

2. Run the project

Unix:
```bash
./mvnw spring-boot:run
```

Windows:
```bash
mvnw spring-boot:run
```


### Option 2: Using Docker

1. Ensure Docker is running.
 
2. Run the following command in the root directory to start the containers:
   ```bash
   docker compose up --build
   ```
3. To stop:
   ```bash
   docker compose down
   ```

## API Endpoints

### Health Metrics

**GET** `/actuator/health`

Returns application health status.

---

### Authentication
**POST** `/api/v1/auth/register`

Registers a new user account.

**Request Body:**

```json
{
  "username": "janedoe",
  "email": "jane@example.com",
  "password": "password123"
}
```

## Roadmap
Upcoming features:
- Login endpoint
- JWT authentication

## Project Challenge Source

This project is inspired by the **Expense Tracker API** challenge provided by roadmap.sh.

[View the original challenge instructions here](https://roadmap.sh/projects/expense-tracker-api)

