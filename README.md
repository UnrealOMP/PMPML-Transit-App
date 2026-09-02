# PMPML Transit Platform

A full-stack public-transit demo for ticket booking and live bus-location visualisation. It is designed as a **modular Spring Boot monolith** with a React single-page application and supporting data services.

> Portfolio project. The included payment, KYC, email, SMS, and bus locations are demo implementations/static data—not production integrations.

## Highlights

- Passenger registration, JWT login, and role-based access control
- Browse routes, stops, buses, and scheduled trips
- Fare calculation, booking, mock payment confirmation, and tickets
- Role-restricted ticket verification for conductors and administrators
- Live map backed by static demo bus positions, Redis cache, and PostgreSQL fallback
- PostgreSQL migrations via Flyway, with Redis, Kafka, and Neo4j available for future expansion
- Docker Compose development environment and starter Kubernetes manifests

## Architecture

```text
React + TypeScript SPA
        |
      NGINX
        |
Spring Boot modular monolith
  |          |          |
PostgreSQL  Redis     WebSocket/STOMP
  |
Flyway migrations + static demo data

Optional/in-progress supporting services: Kafka and Neo4j
```

The backend is one deployable application, not a microservices system. Kafka and Neo4j are included as exploratory infrastructure; the current booking and location flows use PostgreSQL and Redis.

## Technology

| Area | Technology |
| --- | --- |
| Frontend | React 18, TypeScript, Vite, Axios, Leaflet |
| Backend | Java 21, Spring Boot, Spring Security, JPA/Hibernate |
| Authentication | BCrypt passwords and signed JWTs |
| Data | PostgreSQL 16, Flyway, Redis 7 |
| Optional infrastructure | Kafka, Neo4j |
| Local deployment | Docker Compose, NGINX |

## Quick start

### Prerequisites

- Docker Desktop with Docker Compose
- Or Java 21+ and Node.js 18+ for local development

### Docker

Create a local environment file and choose a strong JWT secret:

```bash
set JWT_SECRET=replace-with-a-long-random-base64-secret
docker compose up --build
```

Open:

- Frontend: `http://localhost:3000`
- API health: `http://localhost:8080/api/actuator/health`
- API documentation: `http://localhost:8080/api/swagger-ui.html`
- Kafka UI: `http://localhost:8081`
- Neo4j browser: `http://localhost:7474`

The first application startup applies Flyway migrations. The demo seeds routes, buses, future trip schedules, and three static bus locations for the map.

### Local development

```bash
docker compose up -d postgres redis neo4j zookeeper kafka
cd backend && ./mvnw spring-boot:run
cd frontend && npm ci && npm run dev
```

The Vite development server runs at `http://localhost:5173`.

## Demo accounts

These accounts are solely for a fresh local demo database. Do not use this seed migration in a production database.

| Role | Email | Password |
| --- | --- | --- |
| Admin | `admin@pmpml.gov.in` | `password123` |
| Conductor | `conductor1@pmpml.gov.in` | `password123` |
| Operations | `ops@pmpml.gov.in` | `password123` |
| Passenger | `passenger1@example.com` | `password123` |

## Security model

- Public: authentication, health, public transport catalogue, and WebSocket handshake
- Passenger: own bookings, own payments, and own tickets only
- Conductor/Admin: ticket verification
- Operations/Admin: demo/sensor bus-location updates
- Admin: operational actuator endpoints and administrative transport management

For production, replace demo secrets, disable seed accounts, use a real payment provider with signed webhooks, provision bus-device credentials, set CORS/TLS/network policy, and run managed backups/monitoring.

## Data and demo mode

No physical buses or sensors are required to explore the project. Flyway seeds static bus-location records and the API falls back to PostgreSQL when Redis has no location cached. When hardware is added, feed authenticated device telemetry into `POST /api/v1/gps/update` using an operations/device identity—not a passenger identity.

## Testing and validation

```bash
cd frontend && npm run build
cd backend && ./mvnw test
```

The frontend production build is part of the current verification workflow. Backend tests require a Java/Maven environment and, for integration testing, Docker/Testcontainers access.

## Deployment direction

For a first real deployment, use static hosting/CDN for the frontend, a managed container platform for the backend, managed PostgreSQL, and managed Redis. Add managed Kafka only when asynchronous workloads are implemented with durable event delivery and retry/DLQ handling. Kubernetes is not required at this stage.

## Repository layout

```text
frontend/  React application and NGINX container
backend/   Spring Boot API, migrations, tests
k8s/       Starter Kubernetes manifests
```

## License

Educational/portfolio use. Adapt and secure before production use.
