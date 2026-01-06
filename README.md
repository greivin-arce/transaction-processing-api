# Transaction Processing API

Backend API for processing financial transactions with **strong consistency**, **idempotency**, and **concurrency safety**.  
Designed as a production-ready financial service with clean architecture and robust error handling.

---

## ✨ Key Features

- 🔐 JWT Authentication (Bearer Token)
- 💰 Account balance management
- ➕ Deposits & withdrawals
- 🔁 Idempotent operations
- 📒 Balance history (ledger-style)
- 🔒 Optimistic locking for concurrent updates
- 📄 Pagination & sorting
- 🧪 Integration tests (MockMvc)
- 🐳 Docker & Docker Compose
- 🛠️ Flyway database migrations
- 🚨 Centralized error handling (`@ControllerAdvice`)

---

## 🏗️ Architecture Overview

### Clean layered architecture:
```text
Controller → Service → Repository → Database
```
### Design principles:
- Clear separation of concerns
- Stateless REST endpoints
- Transactional service layer
- Database-level consistency guarantees
- Explicit domain errors mapped to HTTP responses

## 🧰 Tech Stack

- **Java 21**
- **Spring Boot**
- **Spring Data JPA**
- **PostgreSQL**
- **Flyway**
- **Docker / Docker Compose**
- **Maven**

## ▶️ Run Locally

### Prerequisites
- Docker
- Java 21+

### Start infrastructure:
```bash
docker compose up -d
```
### Run the application:
```bash
./mvnw spring-boot:run
```
### The API will be available at:
```bash
http://localhost:8080
```

## 🧪 Tests

### Run integration tests:
```bash
./mvnw clean test
```
### Includes:
	•	Account creation
	•	Deposits & withdrawals
	•	Idempotency validation
	•	Balance history
	•	Concurrency conflict handling


## 📦 Database
	•	PostgreSQL with Flyway migrations
	•	Ledger-style transaction storage
	•	Indexed for balance history queries
	•	Idempotency enforced per account


## 🚧 Future Improvements
	•	API versioning
	•	OpenAPI / Swagger documentation
	•	Metrics & observability
	•	Rate limiting
	•	Multi-currency support


## 👤 Author

### Greivin José Arce Navarro

Software Engineer

