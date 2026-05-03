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
| `APP_EMAIL`             | Gmail address used for sending emails            | `smetechinnovators@gmail.com`                                 |
| `APP_PASSWORD`          | Gmail App Password (not your account password)   | `changeme` — **must be overridden**                           |
| `JWT_SECRET`            | Hex-encoded 256-bit secret for JWT signing       | `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970` — **hardcoded fallback, must be overridden in production** |
| `APP_BASE_URL`          | Base URL used in email verification links        | `https://sme-operations-dza7e5czhdggexfh.canadacentral-01.azurewebsites.net` |
| `APP_DOMAIN`            | Domain used for public storefront links          | `localhost:8080`                                              |
| `CORS_ALLOWED_ORIGINS`  | Comma-separated list of allowed CORS origins | `http://localhost:8080,http://localhost:5173,https://sme-operations.netlify.app,https://sme-operations-dza7e5czhdggexfh.canadacentral-01.azurewebsites.net/` |

### Setting environment variables

**Linux / macOS (bash/zsh):**
```bash
export DB_URL="jdbc:postgresql://pg-23b34967-sihlentshangase06-6d21.b.aivencloud.com:12667/smetech?sslmode=require"
export DB_USERNAME=avnadmin
export DB_PASSWORD=your-db-password
export APP_EMAIL=yourapp@gmail.com
export APP_PASSWORD="xxxx xxxx xxxx xxxx"
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
$env:APP_EMAIL="yourapp@gmail.com"
$env:APP_PASSWORD="xxxx xxxx xxxx xxxx"
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
APP_EMAIL=yourapp@gmail.com
APP_PASSWORD=xxxx xxxx xxxx xxxx
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

## Mail (SMTP)

Email is sent via Gmail SMTP using async delivery with retry logic (3 attempts, exponential backoff: 1s → 2s → 4s).

| Setting              | Value              |
|----------------------|--------------------|
| Host                 | `smtp.gmail.com`   |
| Port                 | `587` (STARTTLS)   |
| Auth                 | Required           |
| Connection timeout   | 10,000 ms          |
| Read timeout         | 10,000 ms          |
| Write timeout        | 10,000 ms          |

Credentials are injected via `APP_EMAIL` and `APP_PASSWORD` environment variables. Use a Gmail App Password — not your account password.

> The test profile disables SMTP entirely and uses a local stub on port 3025.

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

Two CI/CD pipelines deploy to separate Azure App Service instances. Each pipeline triggers on a different branch.

---

### Pipeline 1 — Docker-based deploy (`sme-tech`)

**Workflow file:** `.github/workflows/main_sme-tech.yml`  
**Trigger branch:** `main`  
**Target app:** `sme-tech` (Production slot)

Builds a Docker image, pushes it to Docker Hub, then deploys it to Azure App Service.

| Job | Step | Description |
|-----|------|-------------|
| `build` | Checkout | Checks out the repository |
| | Docker Buildx | Sets up multi-platform build support |
| | Docker Hub login | Authenticates with Docker Hub using `DOCKERHUB_USERNAME` / `DOCKERHUB_TOKEN` secrets |
| | Build & push image | Builds from `sme/Dockerfile`; pushes `latest` and commit-SHA tags to Docker Hub |
| `deploy` | Azure login | Authenticates via OIDC (Workload Identity Federation) |
| | Deploy to App Service | Deploys the image to the `sme-tech` Production slot |

**Docker images** are published to Docker Hub as:
```
<DOCKERHUB_USERNAME>/sme-tech-backend:<commit-sha>
<DOCKERHUB_USERNAME>/sme-tech-backend:latest
```

Deployments use the commit SHA tag (not `latest`) for traceability.

**Required secrets:**

| Secret | Description |
|--------|-------------|
| `DOCKERHUB_USERNAME` | Docker Hub account username |
| `DOCKERHUB_TOKEN` | Docker Hub access token |
| `AZUREAPPSERVICE_CLIENTID_2A69...` | Azure AD client ID for `sme-tech` |
| `AZUREAPPSERVICE_TENANTID_53A7...` | Azure AD tenant ID |
| `AZUREAPPSERVICE_SUBSCRIPTIONID_1E5E...` | Azure subscription ID |

---

### Pipeline 2 — JAR-based deploy (`innovators`)

**Workflow file:** `.github/workflows/dev_innovators.yml`  
**Trigger branch:** `dev`  
**Target app:** `innovators` (Production slot)

Builds a JAR with Maven and deploys it directly to Azure App Service (no Docker).

| Job | Step | Description |
|-----|------|-------------|
| `build` | Checkout | Checks out the repository |
| | Set up Java 21 | Installs Java 21 (Microsoft distribution) |
| | Build with Maven | Runs `mvn -B clean package -DskipTests` from the `sme/` directory |
| | Upload artifact | Uploads `sme/target/*.jar` as the `java-app` artifact |
| `deploy` | Download artifact | Retrieves the built JAR |
| | Azure login | Authenticates via OIDC (Workload Identity Federation) |
| | Deploy to App Service | Deploys `*.jar` to the `innovators` Production slot; startup command: `java -jar /home/site/wwwroot/*.jar` |

**Required secrets:**

| Secret | Description |
|--------|-------------|
| `AZUREAPPSERVICE_CLIENTID_7090...` | Azure AD client ID for `innovators` |
| `AZUREAPPSERVICE_TENANTID_715A...` | Azure AD tenant ID |
| `AZUREAPPSERVICE_SUBSCRIPTIONID_C5B5...` | Azure subscription ID |

---

> For both pipelines, environment variables (DB credentials, JWT secret, mail credentials, CORS origins) must be set in the Azure App Service **Configuration → Application Settings** panel — they are never baked into the build artifact.

---

## API

Base URL: `http://localhost:8080`  
All endpoints are versioned under `/api/v1/`.

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
OpenAPI spec: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Server Environments

The Swagger UI exposes a server selector with the following environments:

| Environment | URL |
|-------------|-----|
| Local Development | `http://localhost:8080` |
| Production | `https://sme-operations-dza7e5czhdggexfh.canadacentral-01.azurewebsites.net` |
| dev | `https://innovators-d2b3gycthabmdnhj.southafricanorth-01.azurewebsites.net` |

---

## Testing

- Unit tests: JUnit 5 + Mockito
- Property-based tests: jqwik
- Integration tests: H2 in-memory DB (activated via `test` Spring profile)

Tests do **not** require any environment variables — the `test` profile overrides all external dependencies.

```bash
./mvnw test
```
