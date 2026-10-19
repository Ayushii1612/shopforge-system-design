# Architecture Baseline

## Package Structure

```text
com.shop
├── catalog/{domain,application,infrastructure}
├── cart/{domain,application,infrastructure}
├── pricing/{domain,application,infrastructure}
├── inventory/{domain,application,infrastructure}
├── order/{domain,application,infrastructure}
├── payment/{domain,application,infrastructure}
├── shipping/{domain,application,infrastructure}
├── user/{domain,application,infrastructure}
├── notification/{domain,application,infrastructure}
└── shared
    ├── domain
    ├── application
    └── infrastructure
```

`domain` contains business types and rules without framework annotations. `application` contains use cases and ports. `infrastructure` contains Spring adapters, REST DTOs/controllers, persistence mappings, and configuration. The shared domain package is the only permitted cross-module domain dependency. `DomainArchitectureTest` enforces this rule.

## Class Diagram (Target Model)

```mermaid
classDiagram
    class Product {
      +ProductId id
      +String name
      +CategoryId categoryId
    }
    class ProductVariant {
      +Sku sku
      +Money listPrice
    }
    class Category
    class Cart {
      +UserId userId
      +List~CartItem~ items
    }
    class CartItem {
      +Sku sku
      +int quantity
    }
    class PriceComponent {
      <<interface>>
      +Money total()
      +List~PriceLine~ breakdown()
    }
    class StockEntry {
      +Sku sku
      +int available
      +int reserved
      +long version
    }
    class Reservation {
      +ReservationId id
      +ReservationStatus status
      +Instant expiresAt
    }
    class Order {
      +OrderId id
      +OrderStatus status
      +long version
      +transitionTo(OrderStatus)
    }
    class OrderItem {
      +Sku sku
      +int quantity
      +Money unitPriceSnapshot
    }
    class Payment {
      +PaymentId id
      +String idempotencyKey
      +PaymentStatus status
    }
    class PaymentStrategy {
      <<interface>>
      +PaymentResult pay(PaymentRequest)
    }
    class Shipment {
      +ShipmentId id
      +ShippingStatus status
    }
    class Money {
      <<value object>>
      +BigDecimal amount
      +Currency currency
    }

    Product "1" --> "1..*" ProductVariant : offers
    Category "1" --> "0..*" Product : classifies
    Cart "1" *-- "0..*" CartItem
    CartItem --> ProductVariant : references SKU
    PriceComponent <|.. PriceComponent : decorator chain
    StockEntry "1" --> "0..*" Reservation : reserved by
    Order "1" *-- "1..*" OrderItem
    OrderItem --> ProductVariant : SKU snapshot
    OrderItem --> Money : price snapshot
    Order "1" --> "0..*" Payment
    PaymentStrategy ..> Payment : executes
    Order "1" --> "0..1" Shipment
```

## ER Diagram (Target Persistence Model)

```mermaid
erDiagram
    USERS ||--o{ ADDRESSES : has
    USERS ||--o| CARTS : owns
    CARTS ||--o{ CART_ITEMS : contains
    PRODUCTS ||--|{ PRODUCT_VARIANTS : offers
    CATEGORIES ||--o{ PRODUCTS : classifies
    PRODUCT_VARIANTS ||--o{ CART_ITEMS : selected_as
    PRODUCT_VARIANTS ||--o{ STOCK_ENTRIES : stocked_as
    STOCK_ENTRIES ||--o{ RESERVATIONS : tracks
    USERS ||--o{ ORDERS : places
    ORDERS ||--|{ ORDER_ITEMS : snapshots
    PRODUCT_VARIANTS ||--o{ ORDER_ITEMS : purchased_as
    ORDERS ||--o{ PAYMENTS : paid_by
    ORDERS ||--o| SHIPMENTS : fulfilled_by
    USERS ||--o{ PAYMENTS : initiates
    OUTBOX_EVENTS ||--o{ PROCESSED_EVENTS : delivered_as

    USERS {
      uuid id PK
      string email UK
      string password_hash
    }
    PRODUCTS {
      uuid id PK
      uuid category_id FK
      string name
      string status
    }
    PRODUCT_VARIANTS {
      uuid id PK
      uuid product_id FK
      string sku UK
      decimal list_price
      string currency
    }
    CARTS {
      uuid id PK
      uuid user_id FK
      bigint version
    }
    CART_ITEMS {
      uuid id PK
      uuid cart_id FK
      string sku FK
      int quantity
    }
    STOCK_ENTRIES {
      uuid id PK
      string sku FK
      string warehouse_id
      int available
      int reserved
      bigint version
    }
    RESERVATIONS {
      uuid id PK
      string sku FK
      uuid order_id
      int quantity
      string status
      timestamp expires_at
    }
    ORDERS {
      uuid id PK
      uuid user_id FK
      string status
      decimal total
      string currency
      bigint version
    }
    ORDER_ITEMS {
      uuid id PK
      uuid order_id FK
      string sku
      int quantity
      decimal unit_price_snapshot
      string currency
    }
    PAYMENTS {
      uuid id PK
      uuid order_id FK
      uuid user_id FK
      string idempotency_key
      string status
      string gateway_ref
    }
    SHIPMENTS {
      uuid id PK
      uuid order_id FK
      string status
      string tracking_number
    }
    OUTBOX_EVENTS {
      uuid id PK
      string aggregate_id
      string event_type
      json payload
      timestamp created_at
      timestamp published_at
    }
    PROCESSED_EVENTS {
      uuid consumer_id PK
      uuid event_id PK
      timestamp processed_at
    }
```

The diagram is conceptual; concrete constraints, indexes, and migration details will be introduced with the owning modules.

## Checkout Sequence (Target Flow)

```mermaid
sequenceDiagram
    actor Customer
    participant API as CheckoutController
    participant Facade as CheckoutFacade
    participant Cart as CartPort
    participant Pricing as PricingPort
    participant Inventory as InventoryPort
    participant Shipping as ShippingPort
    participant Orders as OrderPort
    participant Payment as PaymentPort
    participant Gateway as MockPaymentGateway
    participant Outbox as Outbox

    Customer->>API: POST /api/v1/checkout + Idempotency-Key
    API->>Facade: checkout(user, request, key)
    Facade->>Cart: load active cart
    Cart-->>Facade: cart snapshot
    Facade->>Pricing: quote(cart, coupon)
    Pricing-->>Facade: total + breakdown
    Facade->>Inventory: reserve SKUs in sorted order
    Inventory-->>Facade: reservation IDs or out-of-stock
    Facade->>Shipping: quote(address, shipping option)
    Shipping-->>Facade: method, ETA, fee
    Facade->>Orders: create PAYMENT_PENDING order + price snapshots
    Orders-->>Facade: order ID
    Facade->>Payment: create idempotent payment attempt
    Payment->>Gateway: authorize/charge
    Gateway-->>Payment: result or pending
    Payment-->>Facade: payment status
    Facade-->>API: order + payment status
    API-->>Customer: checkout result

    Gateway->>API: signed payment webhook (may retry)
    API->>Payment: verify and deduplicate event
    Payment->>Orders: transition to PAID on confirmed success
    Payment->>Outbox: append OrderPaid in same DB transaction
    Outbox-->>Inventory: commit reservation after publication
```

The payment network call is not held inside a database transaction. A pending order and expiring stock reservation bridge the external call; webhook/reconciliation completes or compensates the workflow. This is a local saga, not a distributed transaction.