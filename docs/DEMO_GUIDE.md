# Demo Guide

This guide is meant for a live presentation of the Digital Parking System Demo. It focuses on what to say, what to click, and what business value each screen proves.

## Demo Goal

Show that the project can digitize the full parking flow:

- booking
- arrival verification
- cashier processing
- reporting
- administration

## Roles to Use

Use the seeded accounts below on a fresh local database:

| Role | Email | Password |
| --- | --- | --- |
| Super Admin | `superadmin@gmail.com` | `password` |
| Cashier | `cashier@gmail.com` | `password` |
| Customer | `customer@gmail.com` | `password` |

## Recommended Presentation Sequence

### 1. Start From the Public Demo Page

Open `/`

Explain:

- This page introduces the Smart Parking System concept for Meskel Square Parking.
- It frames the project as a modern parking solution with automation, reporting, and integration.
- It communicates the business vision before going into system operations.

Key message:

"This project is not only a payment screen. It is a full digital parking operations platform."

### 2. Show the Admin View

Log in as `Super Admin`.

Open these pages:

- `/dashboard`
- `/users`
- `/roles`
- `/privileges`
- `/service/product`

Talking points:

- The admin dashboard gives a real-time operational summary.
- The platform separates users, roles, and privileges for controlled access.
- Services are configurable, so the system is not limited to one parking product.
- The same platform can support parking, car wash, and other customer services.

Business value:

- better governance
- less manual work
- visible revenue and activity
- scalable service catalog

### 3. Show the Customer Journey

Log in as `Customer`.

Open:

- `/customer`
- `/service/order`

What to demonstrate:

- Browse active services
- Choose a parking-related service
- Select date/time where applicable
- Create an order
- Show order history

Talking points:

- Customers can book before arriving.
- The system checks parking slot availability.
- Order history improves transparency and customer trust.
- This reduces chaos at entry points because appointments are already recorded.

Key business message:

"The customer side reduces queue time and makes parking more predictable."

### 4. Show the Cashier Flow

Log in as `Cashier`.

Open:

- `/cashier`
- `/cashier/report`

What to demonstrate on `/cashier`:

- Search an appointment by plate number
- Accept vehicle arrival
- Trigger PMS sync
- Show active vehicles
- Process payment and exit
- Show transaction history

What to explain:

- The cashier does not need manual ticket tracking.
- Plate-based lookup connects the arriving vehicle to a booked appointment.
- The arrival step moves the order into active processing.
- Payment and exit are recorded centrally, which improves auditability.

What to demonstrate on `/cashier/report`:

- total payments
- total revenue
- today revenue
- payment method split
- recent payment list
- simple 7-day revenue trend

Key business message:

"The cashier interface turns payment handling into a controlled, reportable workflow."

## Best End-to-End Story

If you want one short story for the full demo, use this:

1. A customer books parking in advance.
2. The appointment is saved by the system.
3. The cashier searches the plate when the car arrives.
4. The system accepts arrival and starts the parking session.
5. At exit, the cashier processes payment.
6. Revenue and transaction history immediately appear in reports and dashboards.

## Useful Plate Numbers for Local Demo

The PMS demo controller seeds sample pending vehicles such as:

- `5-67887`
- `2-12345`
- `3-98765`

These are useful when demonstrating the PMS sync and cashier flow.

## Main Business Benefits to Emphasize

- Reduces manual ticketing and paper-based processes.
- Improves visibility of revenue and payment history.
- Supports role-based operations for admin, cashier, and customer users.
- Gives management a dashboard for utilization and performance tracking.
- Creates a foundation for future integration with external PMS, payment gateways, and smart hardware.

## New Requirement To Present

If your boss asks about slot guidance and customer navigation, present it like this:

- The customer will choose both appointment time and parking location.
- The parking location will be structured by `Floor` or `Basement`, then `Zone`, then `Slot`.
- Example booking choice: `Basement B2 / Zone C / Slot 18`.
- After booking, the system will open Google Maps to guide the driver to the parking facility or the correct vehicle entrance.
- Once inside the building, the app will show the exact assigned level and slot details.

Use this sentence during the demo:

"The next enhancement is guided parking assignment, where the customer books a time, selects a basement or floor slot, gets Google Maps navigation to the parking entrance, and then follows in-system guidance to the exact slot."

## Honest Demo Limitations

Keep these points ready if someone asks what is still demo-level:

- Some PMS behavior is simulated for demonstration.
- The Chapa payment integration depends on external connectivity and valid credentials.
- The project is strong as a process demo and internal workflow prototype, but production hardening still needs environment cleanup, secret management, and final deployment preparation.
- Exact indoor slot navigation should not rely only on Google Maps unless the building has supported indoor mapping; for most deployments, Google Maps should handle arrival to the site or entrance and the app should handle the final slot guidance.

## Suggested Closing Statement

"This demo shows that the parking operation can move from manual, fragmented work into one digital workflow covering booking, arrival, payment, and reporting."
