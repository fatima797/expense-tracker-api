\# Expense Tracker API



A Spring Boot REST API designed to help track personal expenses and manage budgets.



\*\*Status:\*\* Under active development (Initial Setup Phase).



\## Tech Stack

\- Java 17

\- Spring Boot 3.5.9

\- Maven 3.9+



\## Prerequisites

Before running the application, ensure you have the following installed:

\- JDK 17 or higher

\- Docker (Optional, only if running via Option 2).



\*Note: You do NOT need Maven installed on your system; this project uses the included Maven Wrapper (`mvnw`).\*



\## Running the Application



\*\*Option 1: Using the Maven Wrapper\*\*



1\. Clone the repository

&nbsp;  ```bash

&nbsp;  git clone https://github.com/fatima797/expense-tracker-api.git

&nbsp;  cd expense-tracker-api

&nbsp;  ```



2\. Run the project

&nbsp;  ```bash

&nbsp;  # On macOS/Linux:

&nbsp;  ./mvnw spring-boot:run



&nbsp;  # On Windows:

&nbsp;  mvnw spring-boot:run

&nbsp;  ```



\*\*Option 2: Using Docker\*\* 

&nbsp;

Ensure you have \*\*Docker\*\* installed.



1. Build the image

```bash

docker build -t expense-tracker-api .

```



2\. Run the Container

```bash

docker run -p 8081:8081 expense-tracker-api

```



\## API Endpoints



\- `GET /health` 



\## Verify it's running



Once the app is running, visit: http://localhost:8081/health



\*\*Expected response:\*\* "OK"









