# E‑Commerce Backend

A Spring Boot–based backend for a simple e‑commerce platform.  
Supports user registration, authentication with JWT, order management, payments, and admin CRUD operations for catalog items.

---

## Features

### User
- Register and login with JWT authentication
- Browse items by category, price, or stock
- Create orders and pay using wallet/credit card
- View own orders and order details

### Admin
- Manage users (list, lookup by email, delete)
- Manage items (create, update, delete, list)
- View all orders with pagination
- Filter orders by status
- Cancel orders

---

## Tech Stack
- **Java 17**
- **Spring Boot 3**
- **Spring Security** (JWT authentication, role‑based access)
- **Spring Data JPA** with Hibernate
- **MySQL** (or any relational DB)
- **Maven/Gradle** build system

---

## API Endpoints

### Authentication
- `POST /api/auth/register` – register new user
- `POST /api/auth/login` – login, returns JWT

### User APIs
- `GET /api/user/items` – list available items
- `POST /api/user/orders` – create new order
- `GET /api/user/orders/{id}` – view order details
- `PUT /api/user/orders/{id}/pay` – pay for an order

### Admin APIs
- `GET /api/admin/users` – list all users
- `GET /api/admin/users/email/{email}` – get user by email
- `DELETE /api/admin/users/{id}` – delete user

- `GET /api/admin/items` – list all items
- `POST /api/admin/items` – create item
- `PUT /api/admin/items/{id}` – update item
- `DELETE /api/admin/items/{id}` – delete item (returns `409 Conflict` if referenced by orders)

- `GET /api/admin/orders?page=0&size=50` – view all orders (paginated)
- `GET /api/admin/orders/status/{status}` – filter orders by status
- `PUT /api/admin/orders/{id}/cancel` – cancel order

---

## Example Requests

### Create Item (Admin)
```http
POST /api/admin/items
Authorization: Bearer <ADMIN_JWT>
Content-Type: application/json

{
  "name": "New Hoodie",
  "description": "Warm, soft hoodie with kangaroo pocket",
  "price": 39.99,
  "stockQuantity": 50,
  "category": "Clothing"
}
```
### Update Item (Admin)
```
PUT /api/admin/items/1
Authorization: Bearer <ADMIN_JWT>
Content-Type: application/json

{
  "name": "Updated Hoodie",
  "description": "Updated description: lighter fabric, same warmth",
  "price": 34.99,
  "stockQuantity": 45,
  "category": "Clothing"
}
```
### Delete Item (Admin)
```
DELETE /api/admin/items/3
Authorization: Bearer <ADMIN_JWT>
```
- Returns 204 No Content if successful, or 409 Conflict if the item is referenced by existing orders.

### Development Notes

- Use @Valid DTOs for input validation.

- Return DTOs instead of entities to avoid serialization cycles and leaking internal fields.

- For production, prefer soft delete for items to preserve order history.

- Admin destructive actions should be audited or restricted.

## Getting Started

#### - 1 Clone the repo
```
git clone https://github.com/fricpto/ecommerce-backend.git
cd ecommerce-backend
```
#### - 2 Configure database  
Update `application.properties` with your DB credentials.

#### - 3 Run the app
```
./gradlew bootRun
```
#### - 2  Test endpoints  
Use Postman, VS Code `.rest` files, or curl with JWT tokens.

### License
MIT License – feel free to use and adapt.
