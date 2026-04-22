# Finx Dashboard

A full-stack personal finance management web application built with **Spring Boot 3** and **Java 17**, demonstrating industry-standard backend practices.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 2.7.18 (LTS) |
| Security | Spring Security 5.7 (Form Login, BCrypt) |
| ORM | Spring Data JPA + Hibernate 5 |
| Database | MySQL 8.0 |
| Template Engine | Thymeleaf 3 |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Build Tool | Gradle 8 |
| Containerization | Docker + Docker Compose |
| Testing | JUnit 5 + Mockito |

## Features

- **Authentication** — Form-based login with Spring Security 6, BCrypt password hashing, session management
- **Dashboard** — Monthly income/expense summary, quick stats, recent transactions
- **Transactions** — Full CRUD with category/direction filtering
- **Cards** — Credit card management with duplicate-number validation
- **Stock Portfolio** — Holdings tracker with P&L calculations
- **Crypto Portfolio** — Asset management with mock price feed
- **REST API** — Fully documented via Swagger UI at `/swagger-ui.html`
- **Exception Handling** — Centralized `@RestControllerAdvice` with structured JSON error responses
- **Input Validation** — Bean Validation (Jakarta) on all request DTOs

## Architecture

```
src/main/java/com/finx/
├── config/          # SecurityConfig, OpenApiConfig
├── controller/      # View controllers (MVC) + REST API controllers
├── dto/
│   ├── request/     # Validated request DTOs (TransactionRequest, StockRequest, …)
│   └── response/    # ApiResponse<T> generic wrapper
├── exception/       # ResourceNotFoundException, BusinessException, GlobalExceptionHandler
├── model/           # JPA entities (User, Account, Transaction, Card, StockHolding, CryptoAsset)
├── repository/      # Spring Data JPA repositories
├── security/        # CustomUserDetails
└── service/         # Business logic layer (UserService, TransactionService, …)
```

## Getting Started

### Prerequisites

- Java 17+
- MySQL 8.0
- Gradle 8 (or use `./gradlew`)

### Local Setup

1. **Create database and seed data**
   ```bash
   mysql -u root -p < sql/schema_full.sql
   ```

2. **Configure credentials**
   ```bash
   cp .env.example .env
   # Edit DB_PASSWORD in .env
   ```

3. **Run**
   ```bash
   ./gradlew bootRun
   ```
   > Windows: `gradlew.bat bootRun`

4. **Access**
   - App: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Login: `james` / `password123`

### Docker Compose (one-command setup)

```bash
cp .env.example .env
docker compose up -d
```

This starts MySQL + the application together. The database is initialised automatically from `sql/schema_full.sql`.

## API Reference

Full interactive documentation available at **`/swagger-ui.html`** after startup.

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions` | Create transaction |
| DELETE | `/api/transactions/{id}` | Delete transaction |
| POST | `/api/cards` | Add credit card |
| DELETE | `/api/cards/{id}` | Remove credit card |
| POST | `/api/stocks` | Add stock holding |
| PUT | `/api/stocks/{id}` | Update stock holding |
| DELETE | `/api/stocks/{id}` | Remove stock holding |
| POST | `/api/crypto` | Add crypto asset |
| PUT | `/api/crypto/{id}` | Update crypto asset |
| DELETE | `/api/crypto/{id}` | Remove crypto asset |

All API endpoints require authentication. Errors follow a consistent JSON structure:

```json
{
  "success": false,
  "message": "Resource not found with id: 99",
  "timestamp": "2025-04-22T14:30:00"
}
```

## Running Tests

```bash
./gradlew test
```

Test report is generated at `build/reports/tests/test/index.html`.

## Database Schema

See [`sql/schema_full.sql`](sql/schema_full.sql) for the complete schema with seed data.

| Table | Description |
|-------|-------------|
| `users` | User accounts with BCrypt-hashed passwords |
| `accounts` | Bank accounts linked to users |
| `transactions` | Financial transactions (income / expense / transfer) |
| `cards` | Credit cards linked to accounts |
| `stock_holdings` | Stock portfolio per user |
| `crypto_assets` | Cryptocurrency holdings per user |
