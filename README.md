# 🚀 Spring Boot Base Project

A professional, production-ready Spring Boot base for modern backend development.

---

## ✨ Features

- **Standardized API Response**: All APIs return a unified `ResponseObject` (`statusCode`, `message`, `data`).
- **Global Exception Handling**: Handles all custom and common Java exceptions, always returns JSON error response.
- **Custom Exception Set**: Includes `BadRequestException`, `ConflictException`, `ForbiddenException`, `UnauthorizedException`, `ValidationException`, etc.
- **Role & Permission System**: Dynamic RBAC with entity-based roles/permissions, JWT contains permission codes.
- **Admin Action Logging**: Aspect-based logging for all admin data changes, with diff, reason, and audit info.
- **OAuth2 & JWT Authentication**: Secure login with Google, Facebook, and JWT for all APIs.
- **Clean Architecture**: Separation of domain, application, infrastructure, and web layers.
- **Swagger/OpenAPI**: Auto-generated, interactive API docs with security requirements.
- **Code-First Data Initialization**: Sample roles, permissions, and users auto-generated on first run.

---

## 🏁 Quick Start

```bash
# Clone the project
$ git clone <your-repo-url>
$ cd demo_login

# Build & run (Maven)
$ ./mvnw spring-boot:run

# Or build JAR
$ ./mvnw clean package
$ java -jar target/*.jar
```

---

## 📚 Project Structure

```
├── config/           # Security, OAuth2, CORS, Filter
├── controller/       # REST API controllers
├── dto/              # Request/response DTOs
├── entity/           # JPA entities (User, Role, Permission, Log, ...)
├── enums/            # Enum types (Role, Status, ...)
├── exception/        # Global handler & custom exceptions
├── initializer/      # DataInitializer (sample data)
├── mapper/           # DTO <-> Entity mappers
├── repository/       # Spring Data JPA repositories
├── service/          # Service interfaces
├── serviceImpl/      # Service implementations
├── aspect/           # AOP for permission & admin log
├── annotation/       # Custom annotations (@RequirePermission, ...)
├── utils/            # Utility classes
└── ...
```

---

## 🔒 API Response Standard

All API responses (success & error) follow:
```json
{
  "statusCode": 200,
  "message": "Success",
  "data": { ... }
}
```

Error example:
```json
{
  "statusCode": 401,
  "message": "Authentication token is missing!",
  "data": null
}
```

---

## 🛡️ Exception Handling

- All exceptions (custom & Java built-in) are globally handled.
- Always returns `ResponseObject` with correct HTTP status and message.
- Easily extend with your own exceptions.

---

## 🧑‍💼 Role & Permission

- Entity-based RBAC: User <-> Role <-> Permission (many-to-many)
- JWT contains `permissionCodes` for fast permission check
- Use `@RequirePermission("PERMISSION_CODE")` on any API
- Sample roles/permissions auto-created on first run

---

## 📝 Admin Action Log

- Use `@AdminAction` annotation on service methods that change data
- All admin actions (update, delete, etc.) are logged with diff, reason, adminId, timestamp
- Log is entity-agnostic, reusable for any domain

---

## 🔑 Authentication

- JWT-based authentication for all APIs
- OAuth2 login with Google, Facebook
- Token refresh, password reset, and more

---

## 📖 Documentation

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- All protected APIs require `Bearer` token (JWT)

---

## 🛠️ Extending

- Add new permissions: update `DataInitializer`
- Add new exception: create class in `exception/exceptions` and add handler in `APIHandleException`
- Add new admin log: annotate service method with `@AdminAction`
- Add new API: always return `ResponseObject`

---

## 💡 Tips

- Use this base for any Spring Boot project needing robust, secure, and maintainable backend.
- All code is ready for production and easy to extend.

---

## 📣 Credits

- Built with Spring Boot, Lombok, Swagger, JPA, AOP, OAuth2, JWT
- Designed for professional, scalable backend development 