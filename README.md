# E‑Commerce Backend

EcommerceApplication — A Spring Boot–based backend for a simple e-commerce platform that serves a single-page frontend (Vite build) and provides JWT-secured REST APIs for user registration, authentication, product catalog management, cart, orders, payments, and admin CRUD operations. Built with Spring Security (JWT + role-based access control), Spring Data JPA (Hibernate), and MySQL.

---

Project Structure
```
├── requests/
│   ├── Admin/
│   │   ├── cleanup.rest
│   │   ├── Create_item.rest
│   │   ├── Delete_item.rest
│   │   ├── force_cleanup.rest
│   │   ├── Update_item.rest
│   │   └── View_all_orders.rest
│   ├── AUTH/
│   │   ├── login.rest
│   │   ├── logout.rest
│   │   └── register_user.rest
│   ├── Catalog and cart/
│   │   ├── Add _quantity_to_cart.rest
│   │   ├── Clear_cart.rest
│   │   ├── get_item_by_id.rest
│   │   ├── get_items.rest
│   │   ├── Remove_from_cart.rest
│   │   ├── Update_quantity.rest
│   │   └── View_cart.rest
│   ├── Orders/
│   │   ├── Add_item_to_cart.rest
│   │   ├── cancle_order_id.rest
│   │   ├── get_user_cart_orders.rest
│   │   ├── List_orders.rest
│   │   ├── Order_detail.rest
│   │   ├── place_order.rest
│   │   └── user_paid_orders.rest
│   ├── Users and payment/
│   │   ├── add_credit_cards_to_user.rest
│   │   ├── admin_get_users.rest
│   │   ├── List_user_cards.rest
│   │   ├── Remove_card_from_user.rest
│   │   └── Simulate_payments.rest
│   ├── get_products.rest
│   └── single_order.rest
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── example/
    │   │           └── ecommerce/
    │   │               ├── controller/
    │   │               │   ├── AdminController.java
    │   │               │   ├── AuthController.java
    │   │               │   ├── BlacklistAdminController.java
    │   │               │   ├── CartController.java
    │   │               │   ├── CheckController.java
    │   │               │   ├── ItemsController.java
    │   │               │   ├── OrderController.java
    │   │               │   ├── PaymentController.java
    │   │               │   ├── ProductsController.java
    │   │               │   ├── SpaController.java
    │   │               │   ├── UserController.java
    │   │               │   └── WalletController.java
    │   │               ├── dto/
    │   │               │   ├── AddItemRequest.java
    │   │               │   ├── CreateItemRequest.java
    │   │               │   ├── ItemDto.java
    │   │               │   ├── PaymentResponse.java
    │   │               │   ├── PayRequest.java
    │   │               │   ├── ProductDto.java
    │   │               │   ├── RegisterRequest.java
    │   │               │   └── SimulatePaymentRequest.java
    │   │               ├── model/
    │   │               │   ├── BlacklistedToken.java
    │   │               │   ├── Cart.java
    │   │               │   ├── CartItem.java
    │   │               │   ├── CreditCard.java
    │   │               │   ├── Item.java
    │   │               │   ├── Order.java
    │   │               │   ├── OrderItem.java
    │   │               │   ├── Payment.java
    │   │               │   ├── PaymentRecord.java
    │   │               │   ├── User.java
    │   │               │   └── Wallet.java
    │   │               ├── repository/
    │   │               │   ├── BlacklistedTokenRepository.java
    │   │               │   ├── CartItemRepository.java
    │   │               │   ├── CartRepository.java
    │   │               │   ├── CreditCardRepository.java
    │   │               │   ├── ItemRepository.java
    │   │               │   ├── OrderItemRepository.java
    │   │               │   ├── OrderRepository.java
    │   │               │   ├── PaymentRecordRepository.java
    │   │               │   ├── PaymentRepository.java
    │   │               │   ├── UserRepository.java
    │   │               │   └── WalletRepository.java
    │   │               ├── security/
    │   │               │   ├── JwtAuthenticationFilter.java
    │   │               │   ├── JwtUtil.java
    │   │               │   ├── SecurityConfig.java
    │   │               │   └── UserDetailsServiceImpl.java
    │   │               └── service/
    │   │                   ├── AdminService.java
    │   │                   ├── AuthService.java
    │   │                   ├── BlacklistCleanupService.java
    │   │                   ├── CartService.java
    │   │                   ├── CheckUserAndCardService.java
    │   │                   ├── OrderService.java
    │   │                   ├── PaymentService.java
    │   │                   ├── UserService.java
    │   │                   └── WalletService.java
    │   └── resources/
    │       ├── keys/
    │       │   ├── private_pkcs8.pem
    │       │   ├── private.pem
    │       │   └── public.pem
    │       ├── static/ (frontend dist)
    │       ├── templates/
    │       └── application.properties
    └── test/
        └── java/
            └── com/
                └── example/
                    └── ecommerce/
                        └── EcommerceApplicationTests.java
```
---
## Features

### User
- Register, login, logout (JWT authentication).
- Browse products and view item details.
- Manage a personal cart: add, remove, update quantities, clear cart.
- Place orders and view order history.
- Add and manage credit cards in a wallet; simulate payments.

### Admin
- List and manage users.
- Create, update, delete items (with checks to prevent deleting items referenced by orders).
- Manage items (create, update, delete, list)
- View and manage all orders.
- Filter orders by status
- Cancel orders
- Trigger manual blacklist cleanup and force purge of blacklisted tokens.

---

## Tech Stack
- **Language:** Java (OpenJDK 25 / toolchain Java 21)
- **Frameworks:** Spring Boot 3.4.0; Spring Data JPA; Spring Security; Spring Scheduling
- **DB:** MySQL (connector: `com.mysql:mysql-connector-j`)
- **JWT:** `io.jsonwebtoken:jjwt` (RSA keys in `resources/keys`)
- **Build:** Gradle (see  build.gradle  )
- **Frontend:** Vite (built `dist` served from `src/main/resources/static`)
- **Testing:** Spring Boot Test, Spring Security Test

---

## API Design (high level)

- **Auth:** `/api/auth/register`, `/api/auth/login`, `/api/auth/logout`
-	**Products/Items:** `/api/products`, `/api/items`, `/api/items/{id}`
-	**Cart:** `/api/user/cart` and subpaths for `add/remove/update/clear`
-	**Orders:** `/api/user/orders` and admin `/api/admin/orders`
-	**Payments:** `/api/user/payments/*`
-	**Admin:** `/api/admin/*` (items, users, orders, blacklist cleanup)


---

## Configuration Notes
-	Scheduling: `@EnableScheduling` required on application class to run `@Scheduled` cleanup.
-	Transactions: Use Spring `@Transactional` for repository delete operations (cleanup).
-	Logging: `spring.security.debug=true` is enabled in `application.properties` for development; disable in production.

## Core Features & Flows

## Authentication
-	Register → `POST /api/auth/register`
-	Login → `POST /api/auth/login` (returns JWT)
-	Logout → `POST /api/auth/logout` (blacklists token until expiry)
## Catalog & Cart
-	List items → GET `/api/items` or GET `/api/products`
-	Item details → GET `/api/items/{id}`
-	Cart operations (user) → POST `/api/user/cart/add`, DELETE `/api/user/cart/remove`, DELETE `/api/user/cart/clear`, PUT `/api/user/cart/items/{itemId}`
## Orders & Payments
-	Place order → POST `/api/user/orders/place`
-	Get user orders → GET `/api/user/orders`
-	Cancel order → DELETE `/api/user/orders/cancel/{orderId}`
-	Mark paid / simulate payment → PUT `/api/user/orders/{orderId}/pay`, POST `/api/user/payments/simulate`

## Admin
-	Manage items & orders → `/api/admin/*` (requires ROLE_ADMIN)
-	Blacklist cleanup (scheduled hourly) and manual endpoints:
    -	DELETE /api/admin/blacklist/cleanup — deletes expired blacklisted tokens
    -	DELETE /api/admin/blacklist/force-cleanup — deletes all blacklisted tokens


## Database Schema & Relationships (summary)
### Key tables / entities and relationships
-	User (users)
    -	`@OneToOne` → Wallet (user owns one wallet)
    -	`@OneToOne` → Cart (user has one cart)
    -	`@OneToMany` → Order (user can have many orders)
-	Wallet (wallets)
    -	`@OneToMany` → CreditCard (wallet contains many credit cards)
-	Cart (carts)
    - `@OneToMany` → CartItem (cart contains many cart items)
    -	`@OneToOne` → User
-	CartItem (cart_items)
    -	`@ManyToOne` → Cart
    -	`@ManyToOne` → Item
-	Order (orders)
    -	`@ManyToOne` → User
    -	`@OneToMany` → OrderItem (cascade ALL, orphanRemoval true)
    -	`@OneToOne` → Payment
-	OrderItem (order_items)
    -	`@ManyToOne` → Order
    -	`@ManyToOne` → Item
-	Item (items)
    -	`@OneToMany` → OrderItem (mappedBy item)
    -	fields: name, description, price, stockQuantity, category, image
-	Payment (payments)
    -	`@OneToOne` → Order
    -	fields: amount, method, status, createdAt
-	BlacklistedToken (blacklisted_tokens)
    -	fields: token, expiry, createdAt — used to reject logged out tokens until expiry
-	PaymentRecord — audit/simulated payment records (uses Instant for created_at)

## Data Validation & Business Rules
-	**Register:** email unique; password required.
-	**Item deletion:** admin prevents deleting items referenced by existing orders (checks `OrderRepository.existsByItemsItemId`).
-	**Cart → Order:** placing an order validates stock, decrements stock, persists `Order` and `OrderItem` rows, clears cart.
-	**Payments:** simulated authorization; do not store CVV; store masked PAN/last4 only.
-	**Blacklisting:** logout stores token + expiry; scheduled cleanup removes expired entries hourly.
## Security Layer & Configuration
-	**JWT** signed with RSA keys (`private_pkcs8.pem` / `public.pem`) in `resources/keys`,`JwtUtil` loads keys and generates/verifies tokens.
-	**Filter:** `JwtAuthenticationFilter` (extends `OncePerRequestFilter`) validates JWT, checks blacklist, and sets `SecurityContext`. Static asset paths are short circuited in the filter to avoid unnecessary processing.
-	**Method security:** `@EnableMethodSecurity` + `@PreAuthorize` on admin endpoints (e.g., `@PreAuthorize("hasRole('ADMIN')")`).
-	**SecurityConfig:** explicit `requestMatchers` for static assets and API routes; do not use `/**.permitAll()` in production. `HttpMethod.OPTIONS` is permitted to avoid preflight blocking when CORS is enabled.
-	**Dev note:** `spring.security.debug=true` is enabled in `application.properties` for troubleshooting; disable in production

## Database (MySQL) — quick DDL hints
-	Use `spring.jpa.hibernate.ddl-auto=update` for development (already set). For production, prefer explicit migrations (Flyway/Liquibase).
-	To reset auto increment after a full purge:
```sql
TRUNCATE TABLE blacklisted_tokens;
-- or
ALTER TABLE blacklisted_tokens AUTO_INCREMENT = 1;
```
## Getting Started

#### - 1 Clone the repo
```
git clone https://github.com/fricpto/ecommerce-backend.git
cd ecommerce-backend
```
#### - 2 Configure database  
Update `src/main/resources/application.properties` with your DB credentials:
```
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_new_password
```
#### - 3 RSA Keys
- To generate them use those commands:
`openssl genrsa -out private.pem 2048` Generates a new 2048‑bit RSA private key and saves it to `private.pem`.
- `openssl rsa -in private.pem -pubout -out public.pem` Extracts the public key from the private key and saves it to `public.pem`.
- `openssl pkcs8 -topk8 -nocrypt -in private.pem -out private_pkcs8.pem` Converts the private key into PKCS#8 format (needed by many Java libraries) without encryption, outputting `private_pkcs8.pem`.
- `openssl pkcs8 -topk8 -nocrypt -in src/main/resources/keys/private.pem -out private_pkcs8.pem` Converts the private key located in `src/main/resources/keys/private.pem` into PKCS#8 format, outputting `private_pkcs8.pem`.
- Place RSA keys in `src/main/resources/keys/` (`private_pkcs8.pem`, `public.pem`).
#### - 4 Optional dev
If running frontend dev server on `http://localhost:5173`, either enable CORS in backend or configure Vite proxy. If you serve the built `dist` from `static`, no CORS is required.
#### - 5 Run the app
```
./gradlew bootRun
```
-	Open the app at `http://localhost:8080/` when serving the built frontend from static. If you run the frontend dev server on `http://localhost:5173`, enable CORS or use a Vite proxy.

#### - 7  Test endpoints  
Use Postman, VS Code .rest files (in the requests/ folder), or curl with JWT tokens to exercise the APIs.

#### Useful Commands
-	Run app: `./gradlew bootRun`
-	Build jar: `./gradlew bootJar`
-	Run tests: `./gradlew test`
-	Reset blacklist table: `TRUNCATE TABLE blacklisted_tokens;`

### License
MIT License – feel free to use and adapt.
