# Expense Tracker API

A Spring Boot REST API designed to help track personal expenses and manage budgets.

**Status:** Under active development (Initial Setup Phase).

## Tech Stack
- Java 17
- Spring Boot 3.5.9
- Maven 3.9+

## Prerequisites
Before running the application, ensure you have the following installed:
- JDK 17 or higher
- Docker (optional, only if running via Option 2)

*Note: You do NOT need Maven installed on your system; this project uses the included Maven Wrapper (`mvnw`).*

## Running the Application

### Option 1: Using the Maven Wrapper

1. Clone the repository:
   ```bash
   git clone https://github.com/fatima797/expense-tracker-api.git
   cd expense-tracker-api
   ```

2. Run the project:
   ```
   # On macOS/Linux:
   ./mvnw spring-boot:run

   # On Windows (PowerShell / CMD):
   mvnw spring-boot:run
   ```

### Option 2: Using Docker
Ensure you have **Docker** installed

1. Build the image:
   ```
   docker build -t expense-tracker-api .
   ```
2. Run the container:
   ```
   docker run -p 8081:8081 expense-tracker-api
   ```

## API Endpoints
- GET /health

## Verify it's running
Once the app is running, visit: http://localhost:8081/health

**Expected response:** OK


## Project Challenge Source

This project is inspired by the **Expense Tracker API** challenge provided by roadmap.sh.

[View the original challenge instructions here](https://roadmap.sh/projects/expense-tracker-api)

