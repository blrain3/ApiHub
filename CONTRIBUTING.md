# Contributing to ApiHub

Thank you for considering contributing! We welcome contributions of all kinds.

## Quick Start

```bash
# Backend (JDK 17+, Maven wrapper)
./mvnw clean package -DskipTests

# Run services (order matters)
java -jar api-auth/target/api-auth-*.jar        # port 8081
java -jar api-admin/target/api-admin-*.jar       # port 8082
java -jar api-invoke/target/api-invoke-*.jar     # port 8083
java -jar api-gateway/target/api-gateway-*.jar   # port 8080

# Frontend (Node 18+)
cd web-admin
npm install
npm run dev     # http://localhost:5173
```

## Environment Variables

Copy `.env.example` and set the required variables before running:

```bash
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=your_jwt_secret_key
export AMAP_KEY=your_amap_api_key
```

## Development Workflow

1. Fork the repository
2. Create a feature branch from `main` (`git checkout -b feat/your-feature`)
3. Make your changes
4. Run tests: `./mvnw test`
5. Commit with conventional commit messages (see below)
6. Push and open a Pull Request

## Commit Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/):

- `feat(scope):` — new feature
- `fix(scope):` — bug fix
- `refactor(scope):` — code restructuring
- `docs(scope):` — documentation
- `chore(scope):` — tooling, config, dependencies

Examples:
```
feat(api-gateway): add HMAC signature verification for open API calls
fix(api-admin): correct datasource YAML indentation
```

## Code Style

- Java 17, Spring Boot 3.3
- 4-space indentation, no tabs
- `Result<T>` for all API responses
- Prefer `JdbcTemplate` over raw JDBC
- Unit tests for shared utilities (`api-common`)