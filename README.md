# RevWorkforce – Human Resource Management System

RevWorkforce is a full-stack **Spring Boot** web application that streamlines core HR operations for a company. It supports three distinct user roles — **Admin**, **Manager**, and **Employee** — each with a dedicated portal and tailored feature set.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.4.3 |
| Web MVC | Spring MVC + Thymeleaf |
| Security | Spring Security + JWT (for REST API) |
| Persistence | Spring Data JPA, Oracle Database |
| Testing | JUnit 4/5, Spring Boot Test, Mockito |
| Build | Maven (Maven Wrapper included) |

---

## 👥 User Roles & Features

### 🔴 Admin
- Register and manage employees (create, edit, deactivate)
- Manage departments, designations, and leave types
- Approve or reject leave applications
- Manage holiday calendar and announcements
- View system logs and generate workforce reports
- Access admin-specific settings and configurations

### 🟡 Manager
- View and manage team members
- Review employee leave requests
- Submit and manage performance reviews and goals for team members
- View team performance reports

### 🟢 Employee
- View personal dashboard with announcements and notifications
- Apply for leaves and track leave balances
- View and update personal profile
- View performance reviews and personal goals
- Access notifications in real time

---

## 🔐 Security

The application uses a **dual security strategy**:

- **Session-based authentication** for all web (Thymeleaf) routes — each role has its own login page (`/login/admin`, `/login/manager`, `/login/employee`).
- **JWT-based authentication** for REST API routes (`/api/**`) — used for Postman/programmatic access. Obtain a token via `POST /api/auth/login`.

---

## ⚙️ Prerequisites

- **Java 17** (JDK 17+)
- **Oracle Database** (an instance running locally or remotely)
- **Maven** — not required if using the included Maven Wrapper (`mvnw`)

---

## 🚀 Setup & Run

### 1. Configure the Database

Open `src/main/resources/application.properties` and update the Oracle datasource credentials:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 2. Initialize the Schema

Run the provided SQL script to create all required tables:

```
src/main/resources/schema.sql
```

This creates tables for: employees, users, departments, designations, leave types, leave applications, leave balances, performance reviews, goals, announcements, events, notifications, holiday calendar, and system logs.

### 3. Build & Start the Application

From the project root directory, run:

```powershell
./mvnw spring-boot:run
```

The application will start on **http://localhost:8080** by default.

---

## 🌐 Accessing the Application

| URL | Description |
|---|---|
| `http://localhost:8080` | Landing page (role selector) |
| `http://localhost:8080/login/admin` | Admin login |
| `http://localhost:8080/login/manager` | Manager login |
| `http://localhost:8080/login/employee` | Employee login |
| `http://localhost:8080/api/**` | REST API endpoints (JWT required) |

> **Default password** for new employees created by the Admin: `Welcome@123`

---

## 🔌 REST API (JWT)

To access the REST API:

1. **Login** via `POST /api/auth/login` with `{"username": "...", "password": "..."}` to receive a JWT token.
2. Include the token in subsequent requests as a Bearer token:
   ```
   Authorization: Bearer <your-jwt-token>
   ```

Available REST controllers:
- `AuthController` — login, register (admin only)
- `EmployeeRestController` — employee data
- `LeaveRestController` — leave management
- `NotificationRestController` — notifications

---

## 📁 Project Structure

```
src/main/java/com/rev/app/
├── controller/        # Thymeleaf MVC controllers (web routes)
├── rest/              # REST API controllers (/api/**)
├── service/           # Business logic (interfaces + implementations)
├── entity/            # JPA entities (DB models)
├── dto/               # Data Transfer Objects
├── repository/        # Spring Data JPA repositories
├── security/          # JWT filter, UserDetailsService, SecurityConfig
├── config/            # Spring configuration classes
├── exceptions/        # Custom exception classes
└── mapper/            # Entity-DTO mappers

src/main/resources/
├── templates/         # Thymeleaf HTML views
│   ├── admin/         # Admin portal views
│   ├── manager/       # Manager portal views
│   ├── leaves/        # Leave management views
│   ├── performance/   # Performance review views
│   └── fragments/     # Reusable navbar, pagination fragments
├── static/            # CSS, JavaScript, images
├── application.properties
└── schema.sql         # Database initialization script
```

---

## 🧪 Running Tests

```powershell
./mvnw test
```

Tests include unit tests for services, validation logic, and REST controller layer tests using `@WebMvcTest` with mocked security.
