# Digital Parking System Demo

This repository contains a demo-ready digital parking platform for Meskel Square Parking. It combines a Vue 3 frontend with a Spring Boot backend to show how parking operations, service booking, cashier flow, and reporting can work in one system.

## What This Project Does

The project demonstrates a smart parking workflow with three main user roles:

- `Super Admin`: manages users, roles, privileges, and service catalog items, and monitors dashboard analytics.
- `Customer`: browses active services, books parking or related services, reviews order history, and completes payment.
- `Cashier`: finds appointments by plate number, accepts vehicle arrival, syncs pending vehicles from PMS, processes payment and exit, and reviews cashier reports.

In the current codebase, the system supports these business capabilities:

- User authentication with JWT-based sessions.
- Role-based navigation and access control.
- User management, role management, and privilege management.
- Service management for configurable offerings such as parking, car wash, and amusement-style services.
- Parking booking with date and time-slot availability checks.
- Order lifecycle tracking with `PENDING`, `PROCESSING`, `COMPLETED`, `FAILED`, and `CANCELLED` states.
- Cashier payment processing and PMS payment history.
- Admin and customer dashboards with revenue and operational summaries.
- Chapa payment initialization and verification endpoints.
- Public-facing demo/procurement page for the smart parking concept.

## Demo Story

This is the clearest way to explain the project during a presentation:

1. A customer logs in and books a parking session or another service.
2. The system checks availability and creates a service order.
3. When the vehicle arrives, the cashier searches the plate number and accepts the arrival.
4. The parking session moves from `PENDING` to `PROCESSING`.
5. At exit, the cashier records the payment, the session is closed, and the transaction is added to reporting.
6. Admin users can monitor users, services, order activity, occupancy, and revenue trends from the dashboard.

## Architecture

### Frontend

- Framework: Vue 3 + Vite
- State: Pinia
- Routing: Vue Router
- HTTP client: Axios
- UI: Tailwind CSS-based components

Key frontend areas:

- `/` public smart parking demo page
- `/dashboard` admin dashboard
- `/users`, `/roles`, `/privileges` administration modules
- `/service/product` service catalog management
- `/service/order` customer service ordering flow
- `/cashier` cashier parking dashboard
- `/cashier/report` cashier payment analytics
- `/customer` customer dashboard

### Backend

- Framework: Spring Boot 3.2
- Language: Java 17
- Security: Spring Security + JWT
- Data access: Spring Data JPA
- Database: PostgreSQL
- API docs: Springdoc OpenAPI / Swagger

Backend base URL:

`http://localhost:8280/api/medco-digital-parking/v1`

## Main Modules

### 1. Authentication and Access Control

The backend exposes authentication endpoints under `/auth/users`, and the frontend protects routes using the authenticated user plus role/privilege metadata.

Important endpoints:

- `POST /auth/users/signIn`
- `POST /auth/users/signUp`
- `GET /auth/users/all`

### 2. User, Role, and Privilege Management

The admin can manage system access through:

- `GET /auth/users/all`
- `POST /auth/role`
- `GET /auth/role/getAll`
- `POST /auth/users/privilege/createPrivilege`
- `GET /auth/users/privilege/list`

### 3. Service Catalog

Admins can create and maintain services with:

- Name
- Description
- Pricing type: `HOURLY` or `FIXED`
- Slot capacity
- Current rate
- Active/inactive status

Important endpoints:

- `GET /services`
- `POST /services`
- `PUT /services/{serviceUuid}`
- `PUT /services/{serviceUuid}/toggle`

### 4. Customer Service Ordering

Customers can:

- View active services
- Check parking availability by date and time
- Select a parking slot
- Create service orders
- Review order history
- Cancel orders if needed

Important endpoints:

- `GET /client/services/active`
- `GET /client/services/{serviceUuid}/availability`
- `POST /client/services/orders/{userUuid}`
- `GET /client/services/orders/history/{userUuid}`
- `PUT /client/services/orders/{orderUuid}/cancel/{userUuid}`

Planned enhancement for parking bookings:

- Let the customer choose a structured slot such as `Basement B1 -> Zone A -> Slot 12`.
- Store floor/basement and zone details separately instead of treating the slot as one free-text field.
- Generate a Google Maps link to the parking facility or the correct gate/entrance.
- Show internal guidance from the entrance to the selected floor/basement and slot.

### 5. Cashier Flow and PMS Sync

Cashier users have a dedicated parking workflow:

- Search a customer appointment by plate number
- Accept parking arrival
- View active parking sessions
- Sync pending vehicles from PMS
- Process payment and exit
- Review cashier-specific payment reports

Important endpoints:

- `GET /client/services/orders/appointment?plate=...`
- `POST /client/services/orders/arrive?plate=...`
- `GET /client/services/orders/active`
- `GET /pms/pending`
- `POST /pms/payments`
- `GET /pms/payments/history`

### 6. Dashboards and Reporting

The backend provides summary data for:

- Total users and active users
- Total and active services
- Total, pending, and completed orders
- Revenue totals and daily revenue trends
- Occupancy snapshot
- Payment success trends
- Peak usage periods
- Recent orders and alerts

Important endpoints:

- `GET /dashboard/summary`
- `GET /dashboard/customer/summary`

### 7. Public Demo Page

The landing page at `/` calls:

- `GET /demo`

This is used to present the smart parking solution concept, procurement view, and high-level feature list.

## Demo Accounts

On an empty database, the backend seeds three users through the startup data loader:

| Role | Email | Password |
| --- | --- | --- |
| Super Admin | `superadmin@gmail.com` | `password` |
| Cashier | `cashier@gmail.com` | `password` |
| Customer | `customer@gmail.com` | `password` |

These accounts are for local demo/development only.

## Local Setup

### Prerequisites

- Node.js 18+ recommended
- Java 17
- Maven Wrapper included in `digital-parking/`
- PostgreSQL running locally

### 1. Start PostgreSQL

Create a database named:

`digital_park`

The backend is currently configured to connect to a local PostgreSQL instance from `digital-parking/src/main/resources/application.properties`.

### 2. Run the backend

```bash
cd digital-parking
./mvnw spring-boot:run
```

The API should start on:

`http://localhost:8280/api/medco-digital-parking/v1`

Swagger UI:

`http://localhost:8280/api/medco-digital-parking/v1/swagger-ui.html`

### 3. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend uses:

- `frontend/src/.env.development`
- `frontend/src/.env.production`

Current local API variable:

`v_API_URL=http://localhost:8280/api/medco-digital-parking/v1`

Note:

- The current frontend codebase mixes `v_API_URL` and `VITE_API_URL` references.
- Several modules also fall back to `http://localhost:8280/api/medco-digital-parking/v1`.
- If you change the backend URL, update these references consistently before packaging the demo.

## Suggested Demo Flow

Use this sequence for presentations:

1. Open `/` and explain the smart parking concept.
2. Sign in as `Super Admin` and show:
   - Dashboard
   - Users
   - Roles and privileges
   - Service management
3. Sign in as `Customer` and show:
   - Customer dashboard
   - Service order screen
   - Parking booking / order history
4. Sign in as `Cashier` and show:
   - Appointment lookup by plate
   - Accept arrival
   - PMS sync
   - Payment processing
   - Cashier report dashboard

There is a dedicated walkthrough in [docs/DEMO_GUIDE.md](/home/john/Documents/medco-project/digital-parking2/docs/DEMO_GUIDE.md).

There is also a feature note for structured parking slot selection and navigation in [docs/SLOT_NAVIGATION_PLAN.md](/home/john/Documents/medco-project/digital-parking2/docs/SLOT_NAVIGATION_PLAN.md).

## Important Notes About the Current Demo

- The PMS pending vehicle queue in `PmsController` is demo-oriented and partially simulated in memory.
- PMS payment history is stored through the application database.
- The Chapa integration is present in the backend, but successful end-to-end payment flow depends on valid external credentials and network access.
- Some frontend naming still reflects earlier project scaffolding, but the implemented feature set is now centered on digital parking.
- The current code already has a `selectedSlot` field on service orders, but it does not yet model building level, basement, zone, and entrance navigation as first-class entities.

## Recommended Next Documentation

If you want to keep improving the handoff materials, the next useful documents would be:

- API endpoint reference by module
- Database schema overview
- User manual with screenshots
- Deployment checklist for staging/production
- Demo script with speaker notes
