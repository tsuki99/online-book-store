# 📚 Online Book Store

A RESTful backend service for an online book store built with Java and Spring Boot.

The application provides functionality for browsing and searching books, managing shopping carts, placing orders, and managing store data through role-based access control.

---

## 📌 Description

Online Book Store is a Spring Boot REST API that simulates the backend of an online bookstore.

Users can browse and search for books, manage their shopping cart, place orders, and view their order history.

Administrators have additional permissions to manage books and categories and update order statuses.

The application uses JWT-based authentication and Spring Security for authorization.

---

## ✨ Features

- User registration and authentication
- JWT-based authentication
- Role-based authorization (`USER` / `ADMIN`)
- CRUD operations for Books
- CRUD operations for Categories
- Book search with dynamic filtering
- Pagination and sorting
- Shopping cart management
- Order placement and order history
- Order status management
- Soft delete for selected entities
- Request validation
- Global exception handling
- Database migrations with Liquibase
- Swagger/OpenAPI documentation
- Unit and integration testing
- Docker containerization
- Deployment to AWS

---

## 🛠 Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- Hibernate
- MySQL
- Liquibase
- MapStruct
- Lombok
- Jakarta Bean Validation
- SpringDoc OpenAPI (Swagger)
- Maven
- Docker
- Docker Compose
- AWS EC2
- AWS RDS
- AWS ECR
- JUnit
- Mockito
- MockMvc
- Spring Security Test
- H2 (testing)
- Testcontainers (integration testing)

---

## 🏗 Architecture

The project follows a layered architecture:
```text
Controller → Service → Repository → Database
↓
DTO
↑
MapStruct
```

DTOs are used to separate API requests/responses from database entities.

MapStruct is used for mapping between entities and DTOs.

The main package structure includes:

```text
onlinebookstore
├── config
├── controller
├── dto
├── exception
├── mapper
├── model
├── repository
├── security
├── service
└── validation
```

---

## 🔐 Authentication & Authorization

The application uses Spring Security with stateless JWT authentication.

Users authenticate through the `/auth/login` endpoint using their email and password.
After successful authentication, the application returns a JWT access token.

The token must be provided in the Authorization header for protected endpoints:

`Authorization: Bearer <JWT_TOKEN>`

Access to endpoints is controlled using user authorities.

`USER`

Users can:

- View books
- Search books
- View categories
- View books by category
- Manage their shopping cart
- Place orders
- View their order history

`ADMIN`

Administrators can:

- Create books
- Update books
- Delete books
- Create categories
- Update categories
- Delete categories
- Update order statuses

---

## 📡 API Overview

### 🔑 Authentication
- `POST   /auth/registration`              - register a new user (Public)
- `POST   /auth/login`                     - authenticate user and receive JWT (Public)

### 📚 Books
- `GET    /books`                          - get all books (USER, pagination + sorting by title)
- `GET    /books/{id}`                     - get book by id (USER)
- `POST   /books`                          - create a new book (ADMIN)
- `PUT    /books/{id}`                     - update a book (ADMIN)
- `DELETE /books/{id}`                     - soft delete a book (ADMIN)
- `GET    /books/search`                   - search books (USER, pagination + sorting by title)

### 🏷 Categories
- `GET    /categories`                     - get all categories (USER, pagination + sorting by name)
- `GET    /categories/{id}`                - get category by id (USER)
- `POST   /categories`                     - create a new category (ADMIN)
- `PUT    /categories/{id}`                - update a category (ADMIN)
- `DELETE /categories/{id}`                - soft delete a category (ADMIN)
- `GET    /categories/{id}/books`          - get books by category (USER, pagination + sorting by title)

### 🛒 Shopping Cart
- `GET    /cart`                           - get current user's shopping cart (USER)
- `POST   /cart`                           - add a book to the shopping cart (USER)
- `PUT    /cart/items/{id}`                - update cart item quantity (USER)
- `DELETE /cart/items/{id}`                - remove a book from the shopping cart (USER)

### 📦 Orders
- `POST   /orders`                         - place an order from the current user's cart (USER)
- `GET    /orders`                         - get user's order history (USER, sorted by order date DESC)
- `GET    /orders/{orderId}/items`         - get all items belonging to an order (USER)
- `GET    /orders/{orderId}/items/{id}`    - get a specific order item (USER)
- `PATCH  /orders/{id}`                    - update order status (ADMIN)

Order history is sorted by order date in descending order by default.

---

## 🔎 Search, Pagination & Sorting

The application supports dynamic book searching using JPA Specifications.

Books can be searched using parameters such as:

- title
- author
- ISBN

Pagination and sorting are supported by the API.

Example:

```http
GET /books/search?titleParts=Harry&page=0&size=6
```

Default page size:

`6`

Maximum page size:

`20`

---

## 🗃 Database

The application uses MySQL as the primary database.

Database schema changes are managed with Liquibase migrations.

The main entities include:

- User
- Role
- Book
- Category
- ShoppingCart
- CartItem
- Order
- OrderItem

The project uses relational mappings including:

- One-to-One
- One-to-Many
- Many-to-One
- Many-to-Many

Soft delete is implemented for several entities using a boolean `isDeleted` flag and Hibernate's `@SQLDelete` / `@SQLRestriction`.

---

## 🧪 Testing

The project contains unit and integration tests covering core application functionality.

Testing tools include:

- JUnit
- Mockito
- MockMvc
- Spring Security Test
- H2
- Testcontainers

Tests were implemented for areas including:

- Book functionality
- Category functionality
- Shopping cart functionality

Testcontainers is used to provide a containerized MySQL database for integration testing.

---

## 🐳 Docker

The application can be run using Docker Compose.

Docker Compose starts:

- MySQL database
- Spring Boot application

The application uses environment variables for database credentials and other sensitive configuration.

Example environment variables:

```text 
MYSQLDB_USER=
MYSQLDB_ROOT_PASSWORD=
MYSQLDB_DATABASE=


MYSQLDB_LOCAL_PORT=
MYSQLDB_DOCKER_PORT=


SPRING_LOCAL_PORT=
SPRING_DOCKER_PORT=


DEBUG_PORT=


JWT_SECRET=
JWT_EXPIRATION=
```

Sensitive configuration should be stored in a local .env file and should not be committed to the repository.

---

## ☁️ AWS Deployment

The application was deployed to `Amazon Web Services (AWS)` using several AWS services.

**Amazon EC2**

EC2 is used as the virtual server where the Dockerized Spring Boot application runs.

**Amazon ECR**

Amazon Elastic Container Registry is used to store the Docker image of the application.

The Docker image is pushed to ECR and then pulled by the EC2 instance.

**Amazon RDS**

Amazon Relational Database Service is used to host the MySQL database separately from the application server.

**Deployment flow**
```text
Local Project
     │
     ▼
Docker Image
     │
     ▼
Amazon ECR
     │
     ▼
Amazon EC2
     │
     ├── Spring Boot Application
     │
     ▼
Amazon RDS
     │
     ▼
MySQL Database
```

---

## 📚 API Documentation

The application uses SpringDoc OpenAPI to generate interactive API documentation.

**Local**
```http
http://localhost:8080/swagger-ui/index.html
```

**Deployed application**
```http
http://ec2-100-26-51-113.compute-1.amazonaws.com/swagger-ui/index.html
```

The deployed Swagger UI can be used to test the application without setting it up locally.

---

## ▶️ How to Run Locally

1. Clone the repository
```
git clone <repository-url>
cd online-book-store
```
2. Configure environment variables

Create a `.env` file based on `.env.sample`.

Configure the required database and JWT properties:

```text
MYSQLDB_USER=your_username
MYSQLDB_ROOT_PASSWORD=your_password
MYSQLDB_DATABASE=online_book_store


MYSQLDB_LOCAL_PORT=3306
MYSQLDB_DOCKER_PORT=3306


SPRING_LOCAL_PORT=8080
SPRING_DOCKER_PORT=8080


DEBUG_PORT=5005


JWT_SECRET=your_secret
JWT_EXPIRATION=your_expiration
```

Do not commit the `.env` file to the repository.

3. Start the application with Docker Compose
`docker compose up --build`

4. Open Swagger UI
```http
http://localhost:8080/swagger-ui/index.html
```

---

## 🌐 How to Test the Deployed Application

The application is available through the deployed Swagger UI:

**AWS Swagger UI**
```http
http://ec2-100-26-51-113.compute-1.amazonaws.com/swagger-ui/index.html
```

Two test accounts are available.

**USER account**
```
Email: userBob@i.ua
Password: userBob123
```

This account can be used to test user functionality such as:
- browsing books
- searching books
- viewing categories
- managing the shopping cart
- placing orders
- viewing order history

**ADMIN account**
```
Email: adminIvan@i.ua
Password: adminIvan123
```

This account can be used to test administrator functionality such as:
- creating books
- updating books
- deleting books
- creating categories
- updating categories
- deleting categories
- updating order statuses

> The credentials above are test accounts created specifically for demonstration purposes.

**Authentication flow**
1. Open /auth/login in Swagger.
2. Log in using one of the test accounts.
3. Copy the returned JWT token.
4. Click Authorize in Swagger UI.
5. Enter:
`Bearer <your-token>`
6. Execute the protected endpoints available to the selected role.

---

## ⚠️ Challenges & Solutions
**JWT Authentication**

Implemented stateless authentication using JWT and Spring Security.
A custom JWT authentication filter validates incoming tokens and loads the authenticated user's authorities into the Spring Security context.

**Role-Based Authorization**

Implemented endpoint-level authorization using Spring Security authorities.
Different operations are available to USER and ADMIN roles.

**Dynamic Book Search**

Implemented dynamic book filtering using Spring Data JPA Specifications, allowing search criteria to be combined without creating a separate repository method for every possible combination.

**Soft Delete**

Implemented logical deletion for several entities using Hibernate's @SQLDelete and @SQLRestriction.
Instead of physically removing records from the database, deleted entities are marked with isDeleted = true and excluded from regular queries.

**Database Migrations**

Liquibase is used to manage database schema changes through versioned migrations.
This makes database changes reproducible across different environments.

**Dockerization**

The application and MySQL database were containerized using Docker and Docker Compose.
The application Docker image uses a multi-stage build to separate the build environment from the final runtime image.

**Testing**

Implemented unit and integration tests using JUnit, Mockito, MockMvc and Testcontainers.
Testcontainers allows integration tests to run against a real MySQL container instead of relying only on an in-memory database.

**AWS Deployment**

The application was containerized and deployed to AWS using ECR, EC2 and RDS.
The Docker image is stored in ECR, the application runs on EC2, and the MySQL database is hosted separately in RDS.
