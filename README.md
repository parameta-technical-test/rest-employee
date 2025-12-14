
---

# Parameta Technical Test – REST & SOAP Integration

## 📌 Overview

This project is a **technical test** that implements a hybrid **REST + SOAP** solution for **employee validation, registration, and information retrieval**, integrating the following capabilities:

* Dynamic validations using **Groovy scripts**
* Communication with an external **SOAP service**
* **PDF generation** and storage in **Amazon S3**
* **JWT-based authentication**
* Email delivery with optional attachments
* **Spring Security**
* Automatic API documentation using **OpenAPI / Swagger**

The solution follows **Clean Architecture**, **SOLID principles**, and **enterprise-grade best practices**.

---

## 🧱 Architecture

```
REST API (Spring Boot)
│
├── Controllers (REST)
│
├── Services
│   ├── Validation (Groovy Scripts)
│   ├── SOAP Client
│   ├── PDF Generator
│   ├── Email Delivery
│   ├── AWS S3 Integration
│   └── Security (JWT)
│
├── Repositories (JPA)
│
├── Mappers (MapStruct)
│
└── Commons Module (DTOs, Entities, Utilities)
```

---

## 🔐 Security

* JWT-based authentication
* Custom `JWTAuthenticationFilter`
* Centralized handling of:

  * Unauthorized access
  * Invalid or revoked tokens
* Token blacklist for logout support
* Public endpoints:

  * `/login/**`
  * `/swagger-ui/**`
  * `/v3/api-docs/**`

All other endpoints require a valid **Bearer token**.

---

## 🧪 Dynamic Validations (Groovy)

Employee validations are executed dynamically using **Groovy scripts stored in the database**.

Features:

* Access to employee request context
* Access to `JdbcTemplate`
* SQL execution support
* Custom validation messages
* Sequential and controlled execution

Main service:

* `GroovieScriptExecutorService`

This approach allows validations to be modified **without redeploying the application**.

---

## 📄 PDF Generation & Management

* PDF generation using **OpenPDF (Lowagie)**
* Includes:

  * Employee personal data
  * Formatted dates
  * Salary information
  * Footer with page numbering
* PDFs are stored in **Amazon S3**
* PDF reference is linked to the employee record
* Conditional retrieval based on system parameters

---

## 📧 Email Delivery

* HTML email delivery using **JavaMailSender**
* Supports:

  * CC
  * BCC
  * File attachments (PDF)
* Fully configurable through **database parameters**:

  * Email subject
  * Email content
  * Copy recipients
  * Attachment rules
* Executed **asynchronously** to avoid blocking REST requests

---

## 🔄 SOAP Integration

* SOAP client implemented using `WebServiceTemplate`
* REST → SOAP mapping handled via dedicated mappers
* JWT token forwarded through SOAP headers
* SOAP responses transformed into REST-friendly JSON responses

---

## 📚 API Documentation

* OpenAPI / Swagger 3 integration
* Security documented using **BearerAuth**
* Clear documentation for:

  * Endpoints
  * DTOs
  * Headers
  * Responses

Access URL:

```
http://localhost:8001/swagger-ui/index.html
```

---

## ⚙️ Configuration

The application is fully configurable using **environment variables** and optional `.env` files.

### Main Configuration Areas

* Database (JPA + HikariCP)
* SMTP server
* JWT security
* SOAP endpoint
* AWS S3
* Logging
* Time zone

Example:

```yaml
server:
  port: 8001

jwt:
  secret: ${JWT_SECRET}

aws:
  region: ${AWS_REGION}
  s3:
    bucket: ${AWS_NAME_BUCKET}
```

---

## 🗄️ Database

* JPA / Hibernate
* Clean repository abstraction
* JPQL and native queries
* System parameters used to control dynamic runtime behavior

---

## 🧪 Testing

* Unit testing with **JUnit 5 + Mockito**
* Coverage includes:

  * Services
  * AWS S3 integrations
  * Groovy validations
  * PDF utilities
* Focus on predictable behavior and exception safety

---

## 🚀 Running the Application

### Requirements

* Java 17+
* Maven
* MySQL
* AWS credentials configured
* SMTP credentials configured

### Run Locally

```bash
mvn clean install
mvn spring-boot:run
```

---

## 🐳 Docker Support

This project includes a **multi-stage Dockerfile** to build and run the application efficiently using Docker.

### Dockerfile Highlights

**Build stage**

* Uses `maven:3.9-eclipse-temurin-17`
* Downloads dependencies in offline mode
* Packages the application as a runnable JAR
* Skips tests for faster container builds

**Runtime stage**

* Uses `eclipse-temurin:17-jdk-jammy`
* Runs only the compiled JAR (lightweight image)
* Optimized JVM memory usage
* Exposes port **8001**

### Dockerfile

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY settings.xml /root/.m2/settings.xml

ENV MAVEN_OPTS="-Xmx768m -XX:MaxMetaspaceSize=256m"

RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/rest-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8001
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]
```

### Build Image

```bash
docker build -t parameta-rest-api .
```

### Run Container

```bash
docker run -p 8001:8001 \
  --env-file .env \
  parameta-rest-api
```

### Benefits

* ✅ Reproducible builds
* ✅ Lightweight runtime image
* ✅ Ready for Kubernetes / ECS / cloud deployments
* ✅ Environment-based configuration support

---

## ✅ Applied Best Practices

* Clear separation of responsibilities
* Interfaces for all services
* Dedicated mappers
* Centralized error handling
* JavaDoc documentation
* Externalized configuration (12-Factor App)

---

## 📌 Author

**Technical test developed for Parameta**
Focused on code quality, maintainability, and enterprise-grade architecture.

---
