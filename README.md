# Inventory and Order Management System

A standalone React and Vite inventory management demo with clearly labeled sample products, orders, and historical sales. It runs independently of the mental-health awareness project.

## Run locally

```sh
npm install
npm run dev
```

Open the local URL printed by Vite and go to `/inventory`.

## Demo workspace

The default `VITE_API_MODE=demo` loads sample records marked **DEMO DATA**. Changes stay in a separate browser-local demo workspace. Use Settings to switch between demo data and an empty local workspace.

## Spring Boot API

The frontend can be pointed at a Spring Boot API with `VITE_API_MODE=spring` and `VITE_API_BASE_URL`. No Spring Boot or MySQL backend is included in this repository, and the API contract must match the backend before switching to Spring mode. The AI assistant remains unavailable until a backend assistant service is connected.

Copy `.env.example` to `.env.local` and adjust public frontend configuration only. Never put credentials or API secrets in Vite variables.
