# Product Service

[![CI](https://github.com/CSCI44092GroupProject/product-service/actions/workflows/ci.yml/badge.svg)](https://github.com/CSCI44092GroupProject/product-service/actions/workflows/ci.yml)
[![Docker](https://github.com/CSCI44092GroupProject/product-service/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/CSCI44092GroupProject/product-service/actions/workflows/docker-publish.yml)
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)

A RESTful microservice for managing product catalogue data, built as part of the **EAP e-commerce backend** system.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [1. Clone the repository](#1-clone-the-repository)
  - [2. Configure environment variables](#2-configure-environment-variables)
  - [3. Start the database](#3-start-the-database)
  - [4. Run the application](#4-run-the-application)
- [Running with Docker](#running-with-docker)
- [Environment Variables](#environment-variables)
- [API Reference](#api-reference)
  - [Health Check](#health-check)
  - [Create Product](#create-product)
  - [Get All Products](#get-all-products)
  - [Get Product by ID](#get-product-by-id)
  - [Delete Product](#delete-product)
- [Error Handling](#error-handling)
- [Testing](#testing)
- [CI/CD Pipeline](#cicd-pipeline)
- [Authors](#authors)

---

## Overview

The Product Service exposes a REST API for CRUD operations on products. It persists data to a PostgreSQL database and is designed to be consumed by other services in the EAP microservices ecosystem.

**Base URL:** `http://localhost:8080/api/products`

**Interactive API docs (Swagger UI):** `http://localhost:8080/swagger-ui.html`

**OpenAPI spec:** `http://localhost:8080/api-docs`

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL 16 |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build tool | Maven 3.9 |
| Containerisation | Docker / Docker Compose |

---

## Prerequisites

| Tool | Minimum version |
|---|---|
| Java (JDK) | 21 |
| Maven | 3.9 |
| Docker & Docker Compose | Docker 24 / Compose v2 |

---

## Project Structure

```
product-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/productservice/
│   │   │   ├── config/            # OpenAPI / Swagger configuration
│   │   │   ├── controller/        # REST controllers
│   │   │   ├── dto/               # Request and response DTOs
│   │   │   ├── entity/            # JPA entities
│   │   │   ├── exception/         # Custom exceptions and global handler
│   │   │   ├── mapper/            # Entity ↔ DTO mappers
│   │   │   ├── repository/        # Spring Data JPA repositories
│   │   │   └── service/           # Service interfaces and implementations
│   │   └── resources/
│   │       └── application.yml    # Application configuration
│   └── test/                      # Unit tests
├── .github/
│   └── workflows/
│       ├── ci.yml                 # Build & test on pull requests
│       └── docker-publish.yml     # Build & push Docker image on merge to main
├── docker-compose.yml             # Local PostgreSQL container
├── Dockerfile                     # Multi-stage production image
├── .env.example                   # Environment variable template
└── pom.xml
```

---

## Getting Started

### 1. Clone the repository

```bash
git clone git@github.com:CSCI44092GroupProject/product-service.git
cd product-service
```

### 2. Configure environment variables

```bash
cp .env.example .env
```

Edit `.env` and set your database credentials if they differ from the defaults (see [Environment Variables](#environment-variables) for all options).

### 3. Start the database

```bash
docker compose up -d
```

This starts a PostgreSQL 16 container named `product-db` and exposes it on port `5432`. A named volume (`postgres_data`) persists data between restarts.

### 4. Run the application

```bash
mvn spring-boot:run
```

The service starts on `http://localhost:8080`. Hibernate will automatically create or update the `product` table on startup (`ddl-auto: update`).

---

## Running with Docker

The service ships with a multi-stage Dockerfile that produces a minimal JRE-based image.

**Build the image:**

```bash
docker build -t product-service:local .
```

**Run the full stack (app + database) together:**

```bash
docker compose up -d                      # start the database
docker run --rm \
  --env-file .env \
  --network host \
  -p 8080:8080 \
  product-service:local
```

> **Note:** The Docker image is automatically built and pushed to Docker Hub on every merge to `main` via the [docker-publish workflow](.github/workflows/docker-publish.yml).

---

## Environment Variables

Copy `.env.example` to `.env` and populate the values before running the service.

| Variable | Default | Description |
|---|---|---|
| `SERVER_PORT` | `8080` | Port the Spring Boot application listens on |
| `DB_URL` | `jdbc:postgresql://localhost:5432/product_db` | JDBC connection URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres` | Database password |
| `POSTGRES_DB` | `product_db` | PostgreSQL database name (used by Docker Compose) |
| `POSTGRES_USER` | `postgres` | PostgreSQL superuser (used by Docker Compose) |
| `POSTGRES_PASSWORD` | `postgres` | PostgreSQL password (used by Docker Compose) |
| `DB_PORT` | `5432` | Host port mapped to the PostgreSQL container |

> Never commit `.env` to version control. It is listed in `.gitignore`.

---

## API Reference

All endpoints are prefixed with `/api/products`. Request and response bodies are `application/json`.

---

### Health Check

```
GET /actuator/health
```

Returns the application and database health status.

**Response `200 OK`**
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

---

### Create Product

```
POST /api/products
```

**Request body**

| Field | Type | Required | Constraints |
|---|---|---|---|
| `name` | string | yes | non-blank |
| `unitPrice` | number | yes | > 0 |
| `description` | string | no | — |
| `category` | string | no | — |
| `stock` | integer | yes | >= 0 |

**Example request**

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "unitPrice": 999.99,
    "description": "A powerful laptop",
    "category": "Electronics",
    "stock": 50
  }'
```

**Response `201 Created`**

```json
{
  "productId": 1,
  "name": "Laptop",
  "unitPrice": 999.99,
  "description": "A powerful laptop",
  "category": "Electronics",
  "stock": 50
}
```

---

### Get All Products

```
GET /api/products
```

**Example request**

```bash
curl http://localhost:8080/api/products
```

**Response `200 OK`**

```json
[
  {
    "productId": 1,
    "name": "Laptop",
    "unitPrice": 999.99,
    "description": "A powerful laptop",
    "category": "Electronics",
    "stock": 50
  }
]
```

Returns an empty array `[]` if no products exist.

---

### Get Product by ID

```
GET /api/products/{id}
```

| Parameter | Type | Description |
|---|---|---|
| `id` | long | Product ID |

**Example request**

```bash
curl http://localhost:8080/api/products/1
```

**Response `200 OK`**

```json
{
  "productId": 1,
  "name": "Laptop",
  "unitPrice": 999.99,
  "description": "A powerful laptop",
  "category": "Electronics",
  "stock": 50
}
```

**Response `404 Not Found`** — when the product does not exist.

---

### Delete Product

```
DELETE /api/products/{id}
```

| Parameter | Type | Description |
|---|---|---|
| `id` | long | Product ID |

**Example request**

```bash
curl -X DELETE http://localhost:8080/api/products/1
```

**Response `204 No Content`** — on success (no body).

**Response `404 Not Found`** — when the product does not exist.

---

## Error Handling

All errors return a consistent JSON envelope:

```json
{
  "timestamp": "2026-06-29T10:15:30.123",
  "status": 404,
  "message": "Product not found with id: 99"
}
```

Validation errors (`400 Bad Request`) include a field-level breakdown:

```json
{
  "timestamp": "2026-06-29T10:15:30.123",
  "status": 400,
  "errors": {
    "name": "Product name is required",
    "unitPrice": "Unit price must be greater than 0"
  }
}
```

| HTTP Status | Cause |
|---|---|
| `400 Bad Request` | Request body fails bean validation |
| `404 Not Found` | Product with the given ID does not exist |
| `500 Internal Server Error` | Unexpected server-side error |

---

## Testing

Unit tests use JUnit 5, Mockito, and Spring's `MockMvc`.

**Run all tests:**

```bash
mvn test
```

**Run tests and generate a coverage report:**

```bash
mvn verify
```

Test classes:

| Class | What it covers |
|---|---|
| `ProductControllerTest` | HTTP layer — request mapping, status codes, validation, error responses |
| `ProductServiceImplTest` | Business logic — service methods, exception throwing |

---

## CI/CD Pipeline

| Workflow | Trigger | What it does |
|---|---|---|
| [CI](.github/workflows/ci.yml) | Pull request → `main` | Compiles the project and runs all tests with `mvn verify` |
| [Docker Publish](.github/workflows/docker-publish.yml) | Push to `main` | Builds the Docker image and pushes it to Docker Hub as `<DOCKERHUB_USERNAME>/product-service:latest` |

**Required GitHub Secrets:**

| Secret | Description |
|---|---|
| `DOCKERHUB_USERNAME` | Docker Hub account username |
| `DOCKERHUB_TOKEN` | Docker Hub access token (not your password) |

---

## Authors

| Name | Student ID |
|---|---|
| Shehan Suraweera | CS/2020/016 |
| Pawan Perera | CS/2020/005 |
