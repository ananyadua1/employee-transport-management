# Employee Transport Management System

A portfolio-ready Java Spring Boot REST API for managing employee transportation. It supports administrators, employees, and drivers with JWT authentication and role-based authorization.

## Technology stack

- Java 17 and Spring Boot 3
- Spring Security with JWT
- Spring Data JPA and PostgreSQL
- Bean Validation and global exception handling
- Swagger/OpenAPI
- JUnit 5 and Mockito
- Docker and Docker Compose

## Features

### Administrator

- Create employees, drivers, and administrators
- Add vehicles and transport routes
- Schedule trips and prevent driver or vehicle conflicts
- Cancel or change trip status
- View trip bookings

### Employee

- Self-register and log in
- View upcoming trips and available seats
- Book a seat and view booking history
- Cancel an eligible booking

### Driver

- View assigned trips
- Start and complete an assigned trip

Booking uses a database transaction and pessimistic row lock to prevent concurrent requests from overbooking the final seat.

## Quick start with Docker

Requirements: Docker Desktop or Docker Engine with Compose.

```bash
docker compose up --build
```

The API starts at `http://localhost:8080`. Swagger UI is available at:

`http://localhost:8080/swagger-ui.html`

Default development administrator:

- Email: `admin@transport.local`
- Password: `Admin@123`

Change the administrator password and JWT secret before any real deployment.

## Run locally without Docker

1. Install Java 17, Maven, and PostgreSQL.
2. Create a database named `transport_db`.
3. Set the configuration variables from `.env.example`.
4. Run:

```bash
mvn spring-boot:run
```

Run tests with:

```bash
mvn test
```

## Typical API workflow

1. Log in as the seeded administrator through `POST /api/auth/login`.
2. Copy the returned token and send it as `Authorization: Bearer <token>`.
3. Create a driver through `POST /api/admin/users` with role `DRIVER`.
4. Create a vehicle and route.
5. Schedule a future trip.
6. Register an employee through `POST /api/auth/register`.
7. Log in as the employee, list trips, and book one.

### Administrator login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@transport.local","password":"Admin@123"}'
```

### Create a driver

```bash
curl -X POST http://localhost:8080/api/admin/users \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ADMIN_TOKEN' \
  -d '{"name":"Ravi Kumar","email":"ravi@example.com","password":"Driver@123","role":"DRIVER"}'
```

### Create a vehicle

```bash
curl -X POST http://localhost:8080/api/admin/vehicles \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ADMIN_TOKEN' \
  -d '{"registrationNumber":"KA01AB1234","model":"Tempo Traveller","capacity":12}'
```

### Create a route

```bash
curl -X POST http://localhost:8080/api/admin/routes \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ADMIN_TOKEN' \
  -d '{"name":"Whitefield to Office","pickupPoint":"Whitefield","dropPoint":"Company Campus","distanceKm":18.5}'
```

### Schedule a trip

Use IDs returned by the previous requests and a future ISO-8601 local date-time.

```bash
curl -X POST http://localhost:8080/api/admin/trips \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ADMIN_TOKEN' \
  -d '{"routeId":1,"vehicleId":1,"driverId":2,"departureTime":"2027-01-10T08:30:00"}'
```

## Main endpoints

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register an employee |
| POST | `/api/auth/login` | Public | Receive a JWT |
| POST | `/api/admin/users` | Admin | Create a user with a role |
| POST | `/api/admin/vehicles` | Admin | Add a vehicle |
| POST | `/api/admin/routes` | Admin | Add a route |
| POST | `/api/admin/trips` | Admin | Schedule a trip |
| PATCH | `/api/admin/trips/{id}/status` | Admin | Change trip status |
| GET | `/api/trips` | Employee/Admin | List upcoming trips |
| POST | `/api/bookings/trip/{tripId}` | Employee | Book a seat |
| GET | `/api/bookings/me` | Employee | View booking history |
| PATCH | `/api/bookings/{id}/cancel` | Employee | Cancel own booking |
| GET | `/api/trips/driver/me` | Driver | View assigned trips |
| PATCH | `/api/trips/{id}/driver-status` | Driver | Start or complete a trip |

## Package structure

```text
config/      security and OpenAPI configuration
controller/  REST endpoints
dto/         validated request and safe response models
entity/      JPA entities and enums
exception/   application errors and response handling
repository/  Spring Data repositories
security/    JWT creation and request filtering
service/     business logic and transactions
```

## Production improvements

For production, replace `ddl-auto: update` with Flyway migrations, use a secrets manager, configure HTTPS/CORS, add refresh-token rotation, rate limiting, monitoring, audit logging, and integration tests using Testcontainers.
