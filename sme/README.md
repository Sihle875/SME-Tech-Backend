# SME Operations Automation System

A multi-tenant SaaS platform for SME owners, automating business registration, online ordering, and customer-facing storefronts.

---

## Prerequisites

- Java 21
- Maven (wrapper included — use `./mvnw`)
- PostgreSQL (Aiven cloud instance — see configuration below)
- Gmail account with an App Password for SMTP

---

## Environment Variables

All secrets are injected via environment variables. **Never hardcode credentials in `application.yaml`.** Each variable has a fallback default for local development, but **production deployments must override all of them**.

| Variable                | Description                                      | Default (local dev only)                                      |
|-------------------------|--------------------------------------------------|---------------------------------------------------------------|
| `DB_URL`                | Full JDBC connection URL (with SSL)              | Aiven PostgreSQL (`smetech` DB, port 12667, `sslmode=require`) |
| `DB_USERNAME`           | PostgreSQL username                              | `avnadmin`                                                    |
| `DB_PASSWORD`           | PostgreSQL password                              | `changeme` — **must be overridden**                           |
| `SPRING_MAIL_USERNAME`  | Gmail address used for sending emails            | `smetechinnovators@gmail.com`                                 |
| `SPRING_MAIL_PASSWORD`  | Gmail App Password (not your account password)   | `changeme` — **must be overridden**                           |
| `JWT_SECRET`            | Hex-encoded 256-bit secret for JWT signing       | Insecure default — **must be overridden in production**       |
| `APP_BASE_URL`          | Base URL used in email verification links        | `https://sme-operations-dza7e5czhdggexfh.canadacentral-01.azurewebsites.net` |
| `APP_DOMAIN`            | Domain used for public storefront links          | `localhost:8080`                                              |
| `CORS_ALLOWED_ORIGINS`  | Comma-separated list of allowed CORS origins | `http://localhost:8080,http://localhost:5173,https://sme-operations.netlify.app,https://sme-operations-dza7e5czhdggexfh.canadacentral-01.azurewebsites.net/` |

### Setting environment variables

**Linux / macOS (bash/zsh):**
```bash
export DB_URL="jdbc:postgresql://pg-23b34967-sihlentshangase06-6d21.b.aivencloud.com:12667/smetech?sslmode=require"
export DB_USERNAME=avnadmin
export DB_PASSWORD=your-db-password
export SPRING_MAIL_USERNAME=yourapp@gmail.com
export SPRING_MAIL_PASSWORD="xxxx xxxx xxxx xxxx"
export JWT_SECRET=your-hex-encoded-secret
export APP_BASE_URL="https://sme-operations-dza7e5czhdggexfh.canadacentral-01.azurewebsites.net"
export APP_DOMAIN="sme-operations-dza7e5czhdggexfh.canadacentral-01.azurewebsites.net"
export CORS_ALLOWED_ORIGINS="http://localhost:8080,http://localhost:5173,https://sme-operations.netlify.app"
```

**Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:postgresql://pg-23b34967-sihlentshangase06-6d21.b.aivencloud.com:12667/smetech?sslmode=require"
$env:DB_USERNAME="avnadmin"
$env:DB_PASSWORD="your-db-password"
$env:SPRING_MAIL_USERNAME="yourapp@gmail.com"
$env:SPRING_MAIL_PASSWORD="xxxx xxxx xxxx xxxx"
$env:JWT_SECRET="your-hex-encoded-secret"
$env:APP_BASE_URL="https://sme-operations-dza7e5czhdggexfh.canadacentral-01.azurewebsites.net"
$env:APP_DOMAIN="sme-operations-dza7e5czhdggexfh.canadacentral-01.azurewebsites.net"
$env:CORS_ALLOWED_ORIGINS="http://localhost:8080,http://localhost:5173,https://sme-operations.netlify.app"
```

**Using a `.env` file (recommended for local dev):**

Create `sme/.env` (already in `.gitignore`):
```
DB_URL=jdbc:postgresql://pg-23b34967-sihlentshangase06-6d21.b.aivencloud.com:12667/smetech?sslmode=require
DB_USERNAME=avnadmin
DB_PASSWORD=your-db-password
SPRING_MAIL_USERNAME=yourapp@gmail.com
SPRING_MAIL_PASSWORD=xxxx xxxx xxxx xxxx
JWT_SECRET=your-hex-encoded-secret
APP_BASE_URL=https://sme-operations-dza7e5czhdggexfh.canadacentral-01.azurewebsites.net
APP_DOMAIN=sme-operations-dza7e5czhdggexfh.canadacentral-01.azurewebsites.net
CORS_ALLOWED_ORIGINS=http://localhost:8080,http://localhost:5173,https://sme-operations.netlify.app
```

Then load it before running:
```bash
export $(cat .env | xargs)
./mvnw spring-boot:run
```

---

## CORS

CORS is configured at the application level via `WebMvcConfig`. Allowed origins are controlled by the `CORS_ALLOWED_ORIGINS` environment variable.

| Variable               | Description                          | Default                                                                                                                                 |
|------------------------|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed origins | `http://localhost:8080,http://localhost:5173,https://sme-operations.netlify.app,https://sme-operations-dza7e5czhdggexfh.canadacentral-01.azurewebsites.net/` |

The following methods are permitted across all `/api/**` routes: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS`.

To override allowed origins for a specific environment, set the variable before starting the application:

**Linux / macOS:**
```bash
export CORS_ALLOWED_ORIGINS="https://your-frontend.com,https://another-origin.com"
```

**Windows (PowerShell):**
```powershell
$env:CORS_ALLOWED_ORIGINS="https://your-frontend.com,https://another-origin.com"
```

> **Note:** If you are deploying behind Azure App Service, set `CORS_ALLOWED_ORIGINS` in **Configuration → Application Settings** rather than managing CORS in the Azure portal's CORS blade — the application handles it directly.

---

## Database

- **Provider**: Azure PostgreSQL (Flexible Server)
- **Host**: `learnwiselydb.postgres.database.azure.com:5432`
- **Database**: `sme_operations_db`
- **SSL**: Not enforced via connection string — configure SSL at the server or driver level if required

Schema is managed by Hibernate (`ddl-auto: update`). No manual migrations needed for development.

> **Note:** The `DB_URL` environment variable should be set to the full JDBC connection string. If your database requires SSL, append `?sslmode=require` to the URL.

> The test profile uses H2 in-memory (`jdbc:h2:mem:testdb`) — no external DB needed to run tests.

---

## Running the Application

```bash
# Run (requires environment variables set)
./mvnw spring-boot:run

# Build (skip tests)
./mvnw clean package -DskipTests

# Run tests (uses H2 in-memory DB — no env vars needed)
./mvnw test
```

---

## Deployment

The application is deployed to **Azure App Service** (`sme-operations`) via GitHub Actions on every push to `main`.

### CI/CD Pipeline (`.github/workflows/main_sme-operations.yml`)

| Step | Description |
|------|-------------|
| Build | Compiles and packages the JAR using `mvn clean package -DskipTests` |
| Deploy | Uploads the JAR to Azure App Service (Production slot) |

**Startup command** used by Azure to launch the application:
```
java -jar /home/site/wwwroot/*.jar
```

> Environment variables (DB credentials, JWT secret, mail credentials, CORS origins) must be configured in the Azure App Service **Configuration → Application Settings** panel — they are not bundled into the JAR.

---

## API

Base URL: `http://localhost:8080`  
All endpoints are versioned under `/api/v1/`.

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
OpenAPI spec: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## Testing

- Unit tests: JUnit 5 + Mockito
- Property-based tests: jqwik
- Integration tests: H2 in-memory DB (activated via `test` Spring profile)

Tests do **not** require any environment variables — the `test` profile overrides all external dependencies.

```bash
./mvnw test
```
