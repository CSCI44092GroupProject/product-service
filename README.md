# Product Service

A Spring Boot microservice for managing product data, part of the EAP e-commerce backend.

## Stack
- Java 21 · Spring Boot 3
- PostgreSQL (Docker)
- Hibernate / JPA
- Swagger / OpenAPI

## Run Locally

```bash
# 1. Copy env template and fill in values
cp .env.example .env

# 2. Start Postgres
docker compose up -d

# 3. Run the service
mvn spring-boot:run
```

API docs: http://localhost:8080/swagger-ui.html

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/products` | Create a product |
| GET | `/api/products/{id}` | Get product by ID |
| DELETE | `/api/products/{id}` | Delete product by ID |
