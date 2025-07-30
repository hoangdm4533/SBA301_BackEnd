# 🚀 Spring Boot Enterprise Base Project

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen?style=for-the-badge&logo=spring-boot)
![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Maven](https://img.shields.io/badge/Maven-3.9+-blue?style=for-the-badge&logo=apache-maven)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue?style=for-the-badge&logo=mysql)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=for-the-badge&logo=docker)

**A comprehensive, production-ready Spring Boot foundation for modern enterprise applications**

[Features](#-features) • [Quick Start](#-quick-start) • [Architecture](#-architecture) • [Security](#-security-system) • [Documentation](#-api-documentation) • [Deployment](#-deployment)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Quick Start](#-quick-start)
- [Project Architecture](#-architecture)
- [Security System](#-security-system)
- [API Documentation](#-api-documentation)
- [Configuration](#-configuration)
- [Development Guide](#-development-guide)
- [Deployment](#-deployment)
- [Contributing](#-contributing)

---

## 🎯 Overview

This is a **enterprise-grade Spring Boot base project** designed to accelerate backend development with **production-ready features** out of the box. Built with modern architectural patterns, comprehensive security, and extensive automation.

### 🏆 Why Choose This Base?

- **🚀 Rapid Development**: Pre-built authentication, authorization, logging, and API structure
- **🛡️ Enterprise Security**: JWT + OAuth2, RBAC, brute force protection, token versioning
- **📊 Complete Observability**: Comprehensive logging, activity tracking, and audit trails
- **🔧 Developer Experience**: Hot reload, detailed documentation, automated testing
- **🌐 Production Ready**: Docker deployment, environment management, scaling considerations

---

## ✨ Key Features

### 🔐 **Advanced Authentication & Authorization**
- **Multi-provider OAuth2**: Google, Facebook integration
- **JWT with Refresh Tokens**: Secure token management with automatic refresh
- **Token Versioning System**: Invalidate all user tokens instantly
- **Brute Force Protection**: Account lockout with configurable thresholds
- **Role-Based Access Control (RBAC)**: Dynamic permissions with entity-level security

### 🛡️ **Enterprise Security**
- **@SecuredEndpoint Annotation**: Method and class-level security
- **IP-based Security**: Track and limit access by IP address
- **Session Management**: Multi-device session control
- **Password Policies**: Configurable strength requirements
- **Security Event Logging**: Complete audit trail for security events

### 📊 **Comprehensive Logging & Monitoring**
- **Activity Logging**: Track all user actions with context
- **Admin Action Logging**: Detailed audit for administrative changes
- **Performance Monitoring**: Request timing and resource usage
- **Error Tracking**: Centralized exception handling and reporting

### 🏗️ **Modern Architecture**
- **Clean Architecture**: Separation of concerns with clear layers
- **Aspect-Oriented Programming**: Cross-cutting concerns handled elegantly
- **Standardized API Responses**: Consistent response format across all endpoints
- **Global Exception Handling**: Unified error handling with proper HTTP status codes

### 🔧 **Developer Experience**
- **Hot Reload**: Fast development iteration
- **Comprehensive Testing**: Unit, integration, and security tests
- **API Documentation**: Auto-generated Swagger/OpenAPI specs
- **Data Initialization**: Sample data for quick setup

---

## 🛠️ Technology Stack

### **Core Framework**
- **Spring Boot 3.3.5** - Main application framework
- **Spring Security 6** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer
- **Spring AOP** - Aspect-oriented programming

### **Database & Persistence**
- **MySQL 8.0+** - Primary database
- **Hibernate** - ORM with optimized queries
- **HikariCP** - High-performance connection pooling

### **Security & Authentication**
- **JWT (JSON Web Tokens)** - Stateless authentication
- **OAuth2** - Third-party authentication
- **BCrypt** - Password hashing
- **CORS** - Cross-origin resource sharing

### **Documentation & Testing**
- **Swagger/OpenAPI 3** - API documentation
- **JUnit 5** - Unit testing framework
- **Testcontainers** - Integration testing with real databases
- **MockMvc** - Web layer testing

### **Build & Deployment**
- **Maven** - Build automation and dependency management
- **Docker & Docker Compose** - Containerization
- **Environment Profiles** - Multi-environment configuration

---

## 🏁 Quick Start

### Prerequisites
- ☕ **Java 17+**
- 🛠️ **Maven 3.9+**
- 🐳 **Docker & Docker Compose** (optional, for easy setup)
- 🗄️ **MySQL 8.0+** (or use Docker)

### 🚀 Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone <your-repo-url>
cd demo_login

# Start the entire stack (app + database)
docker-compose up --build

# Access the application
# API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui/index.html
# Database: localhost:3306 (user: demo, password: demo123)
```

### 🔧 Option 2: Local Development

```bash
# Clone the repository
git clone <your-repo-url>
cd demo_login

# Configure environment variables
cp .env.example .env
# Edit .env with your database and OAuth2 credentials

# Run with Maven
./mvnw spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/demo-login-*.jar
```

### 🎯 First Steps After Setup

1. **Access Swagger UI**: http://localhost:8080/swagger-ui/index.html
2. **Default Admin Account**:
   - Username: `admin`
   - Password: `admin123`
3. **Test Authentication**: Use the `/api/login` endpoint
4. **Explore APIs**: All endpoints are documented in Swagger

---

## 🏗️ Architecture

### 📁 Project Structure

```
src/main/java/com/example/demologin/
├── 🎮 controller/          # REST API controllers
│   ├── AuthenticationController.java
│   ├── SecurityManagementController.java
│   ├── UserActivityLogController.java
│   └── ...
├── 📦 dto/                 # Data Transfer Objects
│   ├── request/           # API request DTOs
│   └── response/          # API response DTOs
├── 🗃️ entity/             # JPA entities
│   ├── User.java
│   ├── Role.java
│   ├── Permission.java
│   └── ...
├── 🔧 service/            # Business logic interfaces
├── 🔧 serviceImpl/        # Business logic implementations
├── 📊 repository/         # Data access layer
├── 🛡️ aspect/            # Cross-cutting concerns
│   ├── SecuredEndpointAspect.java
│   ├── UserActivityAspect.java
│   └── AdminActionLogAspect.java
├── 📝 annotation/         # Custom annotations
│   ├── @SecuredEndpoint
│   ├── @UserActivity
│   └── @AdminAction
├── ⚙️ config/            # Configuration classes
│   ├── SecurityConfig.java
│   ├── CORSConfig.java
│   └── ...
├── 🚨 exception/         # Exception handling
├── 🗺️ mapper/            # Entity-DTO mapping
├── 🔧 utils/             # Utility classes
└── 🏗️ initializer/       # Data initialization
```

### 🎨 Architectural Patterns

#### **1. Clean Architecture**
- **Controller Layer**: Handles HTTP requests/responses
- **Service Layer**: Contains business logic
- **Repository Layer**: Data access abstraction
- **Entity Layer**: Domain models

#### **2. Aspect-Oriented Programming (AOP)**
- **Security Aspects**: `@SecuredEndpoint` for permission checking
- **Logging Aspects**: `@UserActivity` and `@AdminAction` for audit trails
- **Cross-cutting Concerns**: Centralized handling of common functionality

#### **3. Dependency Injection**
- **Constructor Injection**: Using `@RequiredArgsConstructor`
- **Interface Segregation**: Clear separation between interfaces and implementations

---

## 🛡️ Security System

### 🔐 Authentication Flow

The application supports multiple authentication methods:

1. **Traditional Login**: Username/password with JWT tokens
2. **OAuth2 Integration**: Google and Facebook authentication
3. **Token Refresh**: Automatic token renewal for seamless user experience

### 🛡️ Authorization System

#### **@SecuredEndpoint Annotation**
```java
// Method-level security
@GetMapping("/admin/users")
@SecuredEndpoint("USER_MANAGEMENT")
public ResponseEntity<ResponseObject> getUsers() { }

// Class-level security (applies to all methods)
@RestController
@SecuredEndpoint("ADMIN_ACCESS")
public class AdminController { }
```

#### **Permission Hierarchy**
```
SUPER_ADMIN
├── USER_MANAGEMENT
│   ├── USER_CREATE
│   ├── USER_UPDATE
│   └── USER_DELETE
├── SECURITY_MANAGEMENT
│   ├── ACCOUNT_LOCK
│   └── ACCOUNT_UNLOCK
└── SYSTEM_MANAGEMENT
    ├── ROLE_MANAGEMENT
    └── PERMISSION_MANAGEMENT
```

### 🔒 Token Management

#### **JWT Structure**
```json
{
  "sub": "username",
  "permissionCodes": ["USER_MANAGEMENT", "SECURITY_ADMIN"],
  "tokenVersion": 1,
  "iat": 1672531200,
  "exp": 1672534800
}
```

#### **Token Versioning**
- **User Token Invalidation**: Increment user's token version
- **Global Token Invalidation**: Change JWT secret
- **Selective Invalidation**: Target specific sessions

### 🚨 Security Features

#### **Brute Force Protection**
- **Failed Login Tracking**: Monitor failed attempts per user/IP
- **Account Lockout**: Automatic lockout after threshold
- **Progressive Delays**: Increasing delays between attempts
- **Admin Override**: Manual unlock capabilities

#### **Session Management**
- **Multi-device Sessions**: Track active sessions per user
- **Force Logout**: Admin can terminate user sessions
- **Session Analytics**: Track login patterns and anomalies

---

## 📚 API Documentation

### 🌐 Swagger UI
Access comprehensive API documentation at: http://localhost:8080/swagger-ui/index.html

### 📋 API Response Format

All APIs follow a standardized response format:

#### **Success Response**
```json
{
  "statusCode": 200,
  "message": "Operation completed successfully",
  "data": {
    // Response data here
  }
}
```

#### **Error Response**
```json
{
  "statusCode": 401,
  "message": "Authentication required",
  "data": null
}
```

### 🔑 Authentication Requirements

Most endpoints require JWT authentication:
```bash
# Include in request headers
Authorization: Bearer <your-jwt-token>
```

### 📊 Key API Endpoints

#### **Authentication**
- `POST /api/login` - User login
- `POST /api/register` - User registration
- `POST /api/refresh-token` - Token refresh
- `POST /api/google-login` - Google OAuth2
- `POST /api/facebook-login` - Facebook OAuth2

#### **User Management**
- `GET /api/admin/users` - List users (Admin only)
- `PUT /api/admin/users/{id}` - Update user (Admin only)
- `DELETE /api/admin/users/{id}` - Delete user (Admin only)

#### **Security Management**
- `POST /api/admin/security/unlock-account/{username}` - Unlock account
- `GET /api/admin/security/lockouts` - View account lockouts
- `GET /api/admin/security/login-attempts/{username}` - View login attempts

#### **Activity Logs**
- `GET /api/user-activity-logs` - View activity logs
- `POST /api/user-activity-logs/export` - Export logs

---

## ⚙️ Configuration

### 🌍 Environment Variables

Create a `.env` file in the project root:

```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/demo_login
SPRING_DATASOURCE_USERNAME=demo
SPRING_DATASOURCE_PASSWORD=demo123
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-here-make-it-long-and-random
JWT_EXPIRATION_MS=3600000
JWT_REFRESH_EXPIRATION_MS=86400000

# Email Configuration (for password reset, notifications)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

# OAuth2 - Google
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=your-google-client-id
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=your-google-client-secret

# OAuth2 - Facebook
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_FACEBOOK_CLIENT_ID=your-facebook-app-id
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_FACEBOOK_CLIENT_SECRET=your-facebook-app-secret

# Frontend URL (for CORS)
FRONTEND_BASE_URL=http://localhost:3000
```

### 🔧 Application Profiles

#### **Development Profile** (`application-dev.properties`)
```properties
# Enable debug logging
logging.level.com.example.demologin=DEBUG
logging.level.org.springframework.security=DEBUG

# Show SQL queries
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

#### **Production Profile** (`application-prod.properties`)
```properties
# Production logging
logging.level.com.example.demologin=INFO
logging.level.org.springframework.security=WARN

# Production database settings
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=validate
```

---

## 👨‍💻 Development Guide

### 🚀 Adding New Features

#### **1. Adding a New Controller**

```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management")
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    @SecuredEndpoint("PRODUCT_VIEW")
    @UserActivity(activityType = ActivityType.VIEW, details = "View products")
    public ResponseEntity<ResponseObject> getProducts() {
        // Implementation
    }
}
```

#### **2. Adding New Permissions**

1. **Add to DataInitializer**:
```java
// In DataInitializer.java
Permission productView = createPermissionIfNotExists("PRODUCT_VIEW", "View products");
Permission productManage = createPermissionIfNotExists("PRODUCT_MANAGE", "Manage products");
```

2. **Use in Controllers**:
```java
@SecuredEndpoint("PRODUCT_MANAGE")
public ResponseEntity<ResponseObject> createProduct() { }
```

#### **3. Adding Activity Logging**

```java
@Service
public class ProductServiceImpl implements ProductService {
    
    @AdminAction(entityType = "Product", action = "CREATE")
    public Product createProduct(ProductRequest request) {
        // The @AdminAction will automatically log this action
        return productRepository.save(product);
    }
}
```

### 🧪 Testing

#### **Running Tests**
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=SecurityManagementControllerTest

# Run integration tests
./mvnw test -Dtest=**/*IntegrationTest
```

#### **Test Structure**
```
src/test/java/
├── unit/              # Unit tests
├── integration/       # Integration tests
└── security/         # Security-specific tests
```

### 📊 Monitoring & Debugging

#### **Application Logs**
- **Console Output**: Colorful logs with different levels
- **Log Levels**: Configurable per package
- **No File Logging**: Configured to prevent automatic log file creation

#### **Database Monitoring**
```bash
# Connect to database
mysql -h localhost -P 3306 -u demo -p

# View activity logs
SELECT * FROM user_activity_log ORDER BY timestamp DESC LIMIT 10;

# View security events
SELECT * FROM account_lockout WHERE is_active = true;
```

---

## 🚀 Deployment

### 🐳 Docker Deployment

#### **Production Docker Compose**
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/demo_login
    depends_on:
      - db
    
  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: demo_login
      MYSQL_USER: demo
      MYSQL_PASSWORD: demo123
    volumes:
      - mysql_data:/var/lib/mysql
    
volumes:
  mysql_data:
```

#### **Building for Production**
```bash
# Build optimized JAR
./mvnw clean package -Pprod

# Build Docker image
docker build -t demo-login:latest .

# Run with Docker Compose
docker-compose -f docker-compose.prod.yml up -d
```

### ☁️ Cloud Deployment

#### **Render.com Deployment**
1. **Connect GitHub repository**
2. **Configure environment variables**
3. **Set build command**: `./mvnw clean package`
4. **Set start command**: `java -jar target/*.jar`

#### **AWS/Azure/GCP Deployment**
- **Container Registry**: Push Docker image
- **Container Service**: Deploy with environment variables
- **Database**: Use managed MySQL service
- **Load Balancer**: Configure health checks on `/actuator/health`

### 📊 Production Checklist

- [ ] **Environment Variables**: All secrets configured
- [ ] **Database**: Production database with proper credentials
- [ ] **HTTPS**: SSL certificate configured
- [ ] **CORS**: Frontend domains whitelisted
- [ ] **Monitoring**: Application monitoring setup
- [ ] **Backup**: Database backup strategy
- [ ] **Scaling**: Load balancer and multiple instances
- [ ] **Security**: Firewall and security groups configured

---

## 🤝 Contributing

### 🔄 Development Workflow

1. **Fork the repository**
2. **Create feature branch**: `git checkout -b feature/new-feature`
3. **Make changes**: Follow coding standards
4. **Write tests**: Ensure good test coverage
5. **Run tests**: `./mvnw test`
6. **Commit changes**: Use conventional commits
7. **Push to branch**: `git push origin feature/new-feature`
8. **Create Pull Request**: Describe changes clearly

### 📝 Coding Standards

- **Java**: Follow Google Java Style Guide
- **Naming**: Use descriptive names for variables and methods
- **Comments**: Document complex business logic
- **Tests**: Write tests for new features
- **Security**: Always use `@SecuredEndpoint` for protected endpoints

### 🐛 Bug Reports

When reporting bugs, please include:
- **Environment**: OS, Java version, database version
- **Steps to reproduce**: Clear reproduction steps
- **Expected behavior**: What should happen
- **Actual behavior**: What actually happens
- **Logs**: Relevant error messages or stack traces

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙋‍♂️ Support

- **Documentation**: Check this README and code comments
- **Issues**: Create GitHub issues for bugs or feature requests
- **Discussions**: Use GitHub Discussions for questions
- **Email**: [caovanducanh.dev@gmail.com]

---

## 🎉 Acknowledgments

- **Spring Boot Team** - For the amazing framework
- **Spring Security Team** - For robust security features
- **Community Contributors** - For feedback and improvements
- **Open Source Libraries** - That make this project possible

---

<div align="center">

**⭐ If this project helped you, please give it a star! ⭐**

Made with ❤️ by [caovanducanh](https://github.com/caovanducanh)

</div>