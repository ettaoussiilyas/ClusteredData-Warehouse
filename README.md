# ClusteredData Warehouse – FX Deals Processing System

## 📌 Project Overview

This project implements a **Spring Boot–based Data Warehouse** designed to ingest, validate, deduplicate, and persist **FX Deals** coming from external systems (e.g. Bloomberg).

The system ensures:
- No duplicate FX deals are stored
- Strong validation of incoming data
- High test coverage and production-ready architecture
- Easy deployment using Docker

---

## 🏗️ Architecture Overview

The application follows a layered architecture:


**Core Technologies:**
- Java 17
- Spring Boot
- Spring Data JPA (Hibernate)
- PostgreSQL
- Docker & Docker Compose
- Maven
- JUnit / Mockito
- Swagger (OpenAPI)

---

## 🎯 Functional Scope (9 JIRA Tickets)

### CW-01: Project & Infrastructure Setup
- Spring Boot Maven project initialization
- Dependency management (Web, JPA, Validation, Lombok)
- Docker & Docker Compose (PostgreSQL)
- Application profiles (dev / test / prod)
- Logging configuration

---

### CW-02: Database Schema & FX Deal Entity
- FX Deal JPA entity
- Unique constraint on `dealUniqueId`
- Audit fields (createdAt, updatedAt)
- Database schema / migration scripts

**FX Deal Fields:**
- Deal Unique ID
- From Currency (ISO 4217)
- To Currency (ISO 4217)
- Deal Timestamp
- Deal Amount

---

### CW-03: Repository Layer
- Spring Data JPA repository
- Custom methods:
    - `existsByDealUniqueId`
    - `findByDealUniqueId`
- Repository tests using H2

---

### CW-04: Validation Layer
- Field-level validation
- Custom validation rules
- ISO-4217 currency validation
- Timestamp format (ISO-8601)
- Clear validation error messages

---

### CW-05: FX Deal Business Service
- Core business logic
- Duplicate detection
- Transaction management
- Separation of concerns
- Comprehensive error handling

---

### CW-06: REST API (Single & Batch Import)

**Endpoints:**
- `POST /api/v1/fx-deals`
- `POST /api/v1/fx-deals/batch`

**Features:**
- DTO-based request/response
- Proper HTTP status codes (201, 400, 409)
- Batch import summary (success / failed / duplicates)
- Swagger/OpenAPI documentation

---

### CW-07: Error Handling & Logging
- Global exception handling using `@ControllerAdvice`
- Standard error response structure
- Logback configuration
- Structured logging:
    - INFO: normal flow
    - WARN: validation & duplicates
    - ERROR: unexpected failures

---

### CW-08: Testing & Code Quality
- Unit tests (Entity, Repository, Service)
- Integration tests (REST API)
- JaCoCo code coverage
- Minimum coverage: **80%**

---

### CW-09: Docker, Documentation & Final Delivery
- Dockerfile for application
- Full Docker Compose (App + DB)
- Makefile for common commands
- Sample JSON data files
- Clean GitHub repository

---

## 🐳 Running the Application

### Using Docker Compose
```bash
docker-compose up --build
