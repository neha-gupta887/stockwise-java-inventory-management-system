# Stockwise — Inventory and Order Management System

Standalone college project. The interface is a React + Vite app; the API and business rules run in **Java 17 / Spring Boot**, with **MySQL** as the production database. This repository is independent from the Mindbridge mental-health project.

## Run the presentation UI

```sh
npm install
npm run dev
```

Open the Vite URL at `/inventory`. The default `VITE_API_MODE=demo` uses clearly labeled sample records stored in this browser. It is suitable for previewing the UI without a database.

## Run the Java API with an isolated demo database

Install Java 17+ and Maven 3.9+, then in one terminal:

```sh
mvn -f backend/pom.xml spring-boot:run -Dspring-boot.run.profiles=demo
```

The `demo` profile uses an in-memory H2 database and inserts explicitly illustrative sample products. It is reset when the backend stops. In `.env.local`, set `VITE_API_MODE=spring` and `VITE_API_BASE_URL=http://localhost:8080/api`, then restart Vite to exercise the Java API.

## Run with MySQL

Create a MySQL database (or allow the configured local MySQL user to create `stockwise`), set environment variables, then run without the demo profile:

```powershell
$env:DB_URL='jdbc:mysql://localhost:3306/stockwise?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
$env:DB_USERNAME='your-local-user'
$env:DB_PASSWORD='your-local-password'
mvn -f backend/pom.xml spring-boot:run
```

Never commit database credentials. The default profile uses MySQL; only the `demo` profile uses H2.

## Implemented API

| Method | Route | Purpose |
|---|---|---|
| GET / POST | `/api/products` | List and create products |
| PUT / DELETE | `/api/products/{id}` | Update or delete a product (deletion is blocked when order history exists) |
| GET / POST | `/api/orders` | List orders and place an order with transactional stock validation |
| POST | `/api/orders/{id}/cancel` | Cancel a pending order and restore stock transactionally |
| GET | `/api/analytics/sales` | Completed sales grouped by date; empty until completed orders exist |

The assistant and forecasting services are not configured. The UI reports those as unavailable rather than generating pretend AI results. No paid AI, Firebase, or Supabase service is required.

## Configuration

Copy `.env.example` to `.env.local` for public frontend settings. Only `VITE_API_BASE_URL` and mode flags belong in frontend configuration; backend credentials must remain in the backend process environment.
