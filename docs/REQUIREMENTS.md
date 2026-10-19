# Requirements Baseline

## Product Goal

Demonstrate production-minded low-level design in a full-stack e-commerce system. Design clarity, correctness under concurrency, and tests take priority over maximizing feature count.

## Step 1 Scope

- One deployable Spring Boot application, organized as a modular monolith.
- Catalog, cart, pricing, inventory, order, payment, shipping, user, notification, and shared modules.
- Each business module has domain, application, and infrastructure packages.
- Domain code is plain Java and may depend only on Java types and shared domain types.
- Module integration uses application/service ports or domain events; modules do not access one another's persistence repositories.
- REST and persistence adapters are infrastructure concerns; controllers exchange DTOs, not entities.
- PostgreSQL is the system of record. Redis is an optional cache/store, not the authority for order or stock correctness.

## System Qualities

- Prevent overselling and duplicate charges under concurrent requests.
- Preserve an order's purchase-time price and make pricing calculations explainable.
- Make retries, payment webhooks, and asynchronous event delivery idempotent.
- Test domain/application behavior independently and enforce architecture rules in CI.
- Keep the initial architecture deployable as one application while allowing later adapter replacement and module extraction.

## Out of Scope for Step 1

Business use cases, database schema migrations, authentication flows, UI, external gateway integration, and deployment automation are added in subsequent steps. The diagrams describe intended contracts, not implemented endpoints or persistence tables.