# URL Shortener: Specification

## Stack
- Java 21, Spring Boot 3.x (Web, Validation, Data JPA), Maven
- MySQL (primary storage) + Flyway migrations
- Redis (cache + rate limiter) via Spring Data Redis (Lettuce)
- JUnit 5, Mockito, Testcontainers, k6 (load test)
- Docker Compose for MySQL and Redis

## Package structure
controller, service, repository, model, dto, cache, config, exception, util

## Requirements
1. Base62Encoder: encode(long) -> String, decode(String) -> long.
   Alphabet 0-9a-zA-Z. Unit tests for round trip, zero, large values, invalid characters.

2. POST /api/v1/shorten
   - Input: longUrl, optional customAlias, optional expiresAt.
   - Validate: http/https only, max length 2048, alias format [a-zA-Z0-9_-] 3-30 chars,
     expiresAt must be in the future, alias already taken -> 409.
   - Insert row, derive short_code from the generated id (or use customAlias),
     in one transaction. Return the full short URL.

3. GET /{shortCode}
   - Look up by short_code (unique index). Redirect with HTTP 302.
   - Expired -> 410 Gone. Unknown -> 404.
   - With Redis: read-through cache, 24h TTL (or until expiry, whichever is sooner).
   - If Redis is down, fall back to MySQL.

4. Rate limiting
   - Sliding window in Redis (sorted set + Lua script) on POST /api/v1/shorten.
   - 10 requests/minute per IP. Return 429 with a Retry-After header.

5. Click analytics (must not slow down redirects)
   - On each redirect: Redis INCR for the counter, plus an async write of
     (short_code, timestamp, referrer, user-agent/device type) to a clicks table.
   - A scheduled job syncs counters from Redis to MySQL.

6. GET /api/v1/analytics/{shortCode}
   - Return total clicks, clicks per day, top referrers, device breakdown.

## Deliverables
- docker-compose.yml, Flyway migrations
- Unit tests (Base62) and integration tests with Testcontainers:
  create -> redirect, expiry (410), alias conflict (409), rate limit (429)
- README.md with setup steps, architecture diagram, and k6 script comparing
  redirect latency with the Redis cache on vs. off