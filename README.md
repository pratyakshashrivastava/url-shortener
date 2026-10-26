# URL Shortener

Scalable URL shortener built with Java 21, Spring Boot 3, MySQL and Redis.

## Features (in progress)
- Base62 short code generation
- Custom aliases and link expiry
- Redis read-through caching
- Sliding window rate limiting
- Asynchronous click analytics

## Run locally
1. Install Java 21 and Docker Desktop
2. `docker compose up -d`
3. `./mvnw spring-boot:run`

## Tech stack
Java 21, Spring Boot 3, MySQL 8.4, Flyway, Redis, Docker Compose, JUnit 5