# Employee Transport Management System

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![License](https://img.shields.io/badge/License-MIT-blue)

A portfolio-ready Java Spring Boot REST API for managing employee transportation. It supports administrators, employees, and drivers with JWT authentication and role-based authorization.

## Table of Contents

- [Technology Stack](#technology-stack)
- [Features](#features)
- [Architecture](#architecture)
- [Quick Start With Docker](#quick-start-with-docker)
- [Run Locally Without Docker](#run-locally-without-docker)
- [API Workflow](#typical-api-workflow)
- [Main Endpoints](#main-endpoints)
- [Testing](#testing)
- [Security](#security)
- [Production Improvements](#production-improvements)

## Technology Stack

- Java 17
- Spring Boot 3
- Spring Security
- JSON Web Tokens
- Spring Data JPA
- PostgreSQL
- Bean Validation
- Swagger/OpenAPI
- JUnit 5
- Mockito
- Docker
- Docker Compose

## Features

### Administrator

- Create employees, drivers, and administrators
- Add vehicles and transport routes
- Schedule employee transport trips
- Prevent driver and vehicle scheduling conflicts
- Cancel trips or change their status
- View employees booked on a trip

### Employee

- Register and log in
- View upcoming transport trips
- Check available seats
- Book a seat
- View booking history
- Cancel an eligible booking

### Driver

- Log in securely
- View assigned trips
- Start an assigned trip
- Mark an active trip as completed

The booking system uses a database transaction and pessimistic row locking to prevent simultaneous requests from overbooking the final available seat.

## Architecture

The application follows a layered backend architecture:

```text
HTTP Request
     |
     v
Controller -> Service -> Repository -> PostgreSQL
     |            |
     |            +-> Transactions and business rules
     +-> Validation, authentication, and authorization
```

### Domain Relationships

- A `User` has one role: `EMPLOYEE`, `DRIVER`, or `ADMIN`.
- A `Trip` connects a route, vehicle, driver, and departure time.
- A `Booking` connects an employee to a trip.
- A unique database constraint prevents duplicate employee-trip bookings.
- Transactional locking prevents the final seat from being assigned twice.

## Quick Start With Docker

### Requirements

Install:

- Docker Desktop, or
- Docker Engine with Docker Compose

Start the complete application:

```bash
docker compose up --build
```

The application starts at:

```text
http://localhost:8080
```

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

### Default Development Administrator

```text
Email: admin@transport.local
Password: Admin@123
```

Change the administrator password and JWT secret before deploying the application.

## Run Locally Without Docker

### Requirements

- Java 17
- Maven
- PostgreSQL

### 1. Create the Database

Create a PostgreSQL database:

```sql
CREATE DATABASE transport_db;
```

### 2. Configure Environment Variables

Use the values provided in `.env.example`:

```env
DB_URL=jdbc:postgresql://localhost:5432/transport_db
DB_USERNAME=transport
DB_PASSWORD=change-me
JWT_SECRET=replace-with-a-base64-encoded-secret-of-at-least-32-bytes
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=replace-with-a-strong-password
```

### 3. Start the Application

```bash
mvn spring-boot:run
```

## Typical API Workflow

1. Log in as the seeded administrator.
2. Copy the returned JWT.
3. Create a driver.
4. Create a vehicle.
5. Create a transport route.
6. Schedule a future trip.
7. Register an employee.
8. Log in as the employee.
9. View upcoming trips.
10. Book a seat.

### Administrator Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@transport.local","password":"Admin@123"}'
```

Example response:

```json
{
  "token": "JWT_TOKEN",
  "userId": 1,
  "name": "System Admin",
  "email": "admin@transport.local",
  "role": "ADMIN"
}
```

Include the token in protected requests:

```text
Authorization: Bearer JWT_TOKEN
```

### Create a Driver

```bash
curl -X POST http://localhost:8080/api/admin/users \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ADMIN_TOKEN' \
  -d '{
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "password": "Driver@123",
    "role": "DRIVER"
  }'
```

### Create a Vehicle

```bash
curl -X POST http://localhost:8080/api/admin/vehicles \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ADMIN_TOKEN' \
  -d '{
    "registrationNumber": "KA01AB1234",
    "model": "Tempo Traveller",
    "capacity": 12
  }'
```

### Create a Route

```bash
curl -X POST http://localhost:8080/api/admin/routes \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ADMIN_TOKEN' \
  -d '{
    "name": "Whitefield to Office",
    "pickupPoint": "Whitefield",
    "dropPoint": "Company Campus",
    "distanceKm": 18.5
  }'
```

### Schedule a Trip

Use the IDs returned by the previous requests and provide a future ISO-8601 date and time.

```bash
curl -X POST http://localhost:8080/api/admin/trips \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ADMIN_TOKEN' \
  -d '{
    "routeId": 1,
    "vehicleId": 1,
    "driverId": 2,
    "departureTime": "2027-01-10T08:30:00"
  }'
```

### Register an Employee

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Ananya Dua",
    "email": "ananya@example.com",
    "password": "Employee@123"
  }'
```

### View Available Trips

```bash
curl http://localhost:8080/api/trips \
  -H 'Authorization: Bearer EMPLOYEE_TOKEN'
```

### Book a Trip

```bash
curl -X POST http://localhost:8080/api/bookings/trip/1 \
  -H 'Authorization: Bearer EMPLOYEE_TOKEN'
```

### View Booking History

```bash
curl http://localhost:8080/api/bookings/me \
  -H 'Authorization: Bearer EMPLOYEE_TOKEN'
```

### Cancel a Booking

```bash
curl -X PATCH http://localhost:8080/api/bookings/1/cancel \
  -H 'Authorization: Bearer EMPLOYEE_TOKEN'
```

## Main Endpoints

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register an employee |
| POST | `/api/auth/login` | Public | Log in and receive a JWT |
| POST | `/api/admin/users` | Admin | Create a user with a role |
| GET | `/api/admin/users` | Admin | View all users |
| POST | `/api/admin/vehicles` | Admin | Add a vehicle |
| GET | `/api/admin/vehicles` | Admin | View all vehicles |
| POST | `/api/admin/routes` | Admin | Add a route |
| GET | `/api/admin/routes` | Admin | View all routes |
| POST | `/api/admin/trips` | Admin | Schedule a trip |
| PATCH | `/api/admin/trips/{id}/status` | Admin | Change trip status |
| GET | `/api/admin/trips/{id}/bookings` | Admin | View trip bookings |
| GET | `/api/trips` | Employee/Admin | View upcoming trips |
| GET | `/api/trips/{id}` | Authenticated | View a specific trip |
| POST | `/api/bookings/trip/{tripId}` | Employee | Book a seat |
| GET | `/api/bookings/me` | Employee | View booking history |
| PATCH | `/api/bookings/{id}/cancel` | Employee | Cancel a booking |
| GET | `/api/trips/driver/me` | Driver | View assigned trips |
| PATCH | `/api/trips/{id}/driver-status` | Driver | Start or complete a trip |

## Package Structure

```text
src/main/java/com/ananya/transport/
├── config/
│   ├── DataInitializer.java
│   ├── OpenApiConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── AdminController.java
│   ├── AuthController.java
│   ├── BookingController.java
│   └── TripController.java
├── dto/
│   ├── AdminDtos.java
│   ├── AuthDtos.java
│   └── ResponseDtos.java
├── entity/
│   ├── Booking.java
│   ├── TransportRoute.java
│   ├── Trip.java
│   ├── User.java
│   └── Vehicle.java
├── exception/
│   ├── BusinessException.java
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── repository/
├── security/
├── service/
└── TransportApplication.java
```

## Testing

The test suite uses JUnit 5 and Mockito.

It verifies important booking behaviour, including:

- Successful seat reservation
- Rejection when a trip is full
- Booking business rules
- Service-layer behaviour

Run all tests:

```bash
mvn clean test
```

## Security

The project implements the following security measures:

- Password hashing with BCrypt
- JWT-based authentication
- Role-based endpoint authorization
- Request validation using DTOs
- Centralized exception handling
- Safe response DTOs that never expose password hashes
- Ownership checks for booking cancellation
- Driver assignment checks for trip updates
- Transactional protection against seat overbooking

The credentials and JWT key included in `docker-compose.yml` are intended only for development. Replace them before deployment and never commit production secrets.

## Resume Highlights

- Built a Spring Boot backend with RESTful APIs for employee ride booking, route creation, vehicle management, and trip tracking.
- Implemented JWT authentication and role-based authorization for employees, drivers, and administrators.
- Used Spring Data JPA and PostgreSQL with transactional locking to prevent concurrent seat overbooking.
- Added validation, centralized exception handling, Swagger documentation, unit testing, and Docker-based deployment.

## Production Improvements

Potential production improvements include:

- Replace `ddl-auto: update` with Flyway database migrations
- Store credentials in a secrets manager
- Configure HTTPS and restrictive CORS policies
- Implement access and refresh-token rotation
- Add API rate limiting
- Add audit logging
- Add application monitoring and health checks
- Use Testcontainers for PostgreSQL integration tests
- Add CI/CD using GitHub Actions
- Deploy using AWS, Railway, Render, or another cloud platform

## License

This project is available under the MIT License. Add a `LICENSE` file before publishing the repository if you want to distribute it under these terms.
