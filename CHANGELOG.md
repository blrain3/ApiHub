# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-08-25

### Added

- **api-common**: Shared `Result<T>` response wrapper, `ErrorCode` enum (SUCCESS / BAD_REQUEST / UNAUTHORIZED / FORBIDDEN / NOT_FOUND / TOO_MANY_REQUESTS / 10001–10009 business codes), `JwtUtil` (HMAC-SHA256 JWT), `BizException`, `ApiHeaders` / `RequestAttrs` constants
- **api-gateway**: Request tracing (`TraceIdFilter`), JWT authentication (`JwtAuthFilter`), Open API HMAC-SHA256 signature verification (`OpenApiAuthFilter`), Nonce replay prevention (Redis + in-memory fallback), QPS / daily quota rate limiting, proxy routing to downstream services (`GatewayProxyFilter`), async invoke log writer
- **api-auth**: User registration (BCrypt password encoding), login (JWT issuance), token refresh / validation, role-based response
- **api-admin**: App management (CRUD, AppId/Secret generation), interface asset management (CRUD, status toggle), grant/revoke interface permissions, `MyBatis-Plus` integration
- **api-invoke**: Weather API proxy (Amap integration), geocode resolution, health check endpoint
- **web-admin**: Vue 3 + Vite + Element Plus admin console; login/register, dashboard, app management page, interface marketplace, invoke log explorer, analytics charts (ECharts), role-based permissions, mock fallback for offline demo

### Changed

- `api-gateway`: Datasource YAML indentation corrected (spring.datasource conf was misplaced under spring.application)
- `api-gateway`: JWT secret aligned with api-auth for consistent token validation
- `api-auth`: Route paths unified with `/api/auth/` prefix

### Fixed

- `api-gateway`: Failed to start with "Failed to determine a suitable driver class" due to wrong datasource YAML indentation

### Security

- API key: Amap weather key moved to environment variable (`AMAP_KEY`)
- Database credentials: MySQL password moved to environment variable (`DB_PASSWORD`)
- JWT secret: Moved to environment variable (`JWT_SECRET`)
- All passwords / secrets removed from tracked configuration files
- `.env.example` added with required environment variable documentation

## [0.1.0] - 2026-08-18

### Added

- Project skeleton: multi-module Maven (Spring Boot 3.3 / Java 17), Vue 3 + Vite frontend
- Docker Compose: MySQL 8.0 + Redis 7
- Initial JWT authentication and gateway proxy filter
- Weather API stub with Amap integration
- Vue 3 admin dashboard, analytics, and app management UI
- Role-based access control (ADMIN / USER)