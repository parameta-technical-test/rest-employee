# Parameta Technical Test – REST & SOAP Integration

## 📌 Overview

This repository contains a **technical test** that delivers a hybrid **REST + SOAP** solution for **employee validation, registration, and information retrieval**.

It includes:

- **REST API (Spring Boot)** used as the main entrypoint for clients
- Integration with an internal/external **SOAP service** (via `WebServiceTemplate`)
- **Dynamic validations** executed with **Groovy scripts stored in the DB**
- **PDF generation** (OpenPDF) and storage in **Amazon S3**
- **JWT-based authentication** with **token revocation** (blacklist)
- **Email delivery** (SMTP) with CC/BCC and optional PDF attachments
- **OpenAPI / Swagger** documentation

The solution follows **Clean Architecture**, **SOLID principles**, and focuses on maintainability and enterprise-grade practices.

---

## 🧱 Modules

- **rest**: REST API (main module)
- **soap**: SOAP service (Spring-WS) *(separate module in the ecosystem)*
- **commons**: Shared library (DTOs, entities, mappers, security utilities, exceptions, helpers)

---

## 🧭 Architecture (REST Module)

```

REST API (Spring Boot)
│
├── Controllers (REST)
│
├── Services
│   ├── Groovy Validation Executor (DB-driven rules)
│   ├── SOAP Client (WebServiceTemplate)
│   ├── PDF Generator (OpenPDF)
│   ├── Email Sender (JavaMailSender)
│   ├── AWS S3 Integration
│   └── Security (JWT + blacklist)
│
├── Repositories (JPA)
│
├── Mappers (MapStruct + custom defaults)
│
└── commons (DTOs, entities, security helpers)

````

---

## 🔐 Security

- **JWT authentication**
- Custom `JWTAuthenticationFilter`
- Token revocation using a **blacklist** table (logout support)
- Standardized 401/403 responses using:
  - `CustomAuthenticationEntryPoint`
  - `CustomAccessDeniedHandler`

### Public endpoints

- `/login/**`
- `/swagger-ui/**`
- `/v3/api-docs/**`

All other endpoints require a valid:

```text
Authorization: Bearer <JWT>
````

---

## 🧪 Dynamic Validations (Groovy)

Employee validations are executed through **Groovy scripts stored in the database**.

Key features:

* Access to request context (employee payload)
* Access to `JdbcTemplate` (SQL-based validations)
* Custom validation messages
* Ordered and controlled execution flow

Main service:

* `GroovieScriptExecutorService`

✅ This allows updating validation rules **without redeploying** the application.

---

## 🔄 SOAP Integration (REST → SOAP)

* SOAP calls are performed using **`WebServiceTemplate`**
* REST DTOs are mapped to SOAP request objects using dedicated mappers
* The **JWT token can be forwarded** when required by the SOAP layer
* SOAP responses are adapted back into REST-friendly JSON responses

Configuration property used by REST to reach SOAP:

```yaml
soap:
  service:
    endpoint: ${SOAP_SERVICE_ENDPOINT}
```

---

## 📄 PDF Generation & S3 Storage

* PDFs generated using **OpenPDF (Lowagie)**
* Includes employee data, formatted dates, salary, and footer
* Output is uploaded to **Amazon S3**
* The employee record stores the **S3 key** (`storageLocationReport`)
* Retrieval can be **enabled/disabled** via system parameters (DB-driven behavior)

---

## 📧 Email Delivery (SMTP)

* Emails are sent using **Spring Boot Mail (`JavaMailSender`)**
* Supports:

  * HTML content
  * **CC / BCC**
  * Attachments (PDF)
* Subject/body/recipients can be controlled using **DB system parameters**
* Sending is executed **asynchronously** to avoid blocking REST requests

Mail settings:

```yaml
spring:
  mail:
    host: ${HOST_SERVER_SMTP}
    port: ${PORT_SERVER_SMTP}
    username: ${USERNAME_SERVER_SMTP}
    password: ${PASSWORD_SERVER_SMTP}
    properties:
      mail:
        smtp:
          auth: ${AUTH_SERVER_SMTP}
          starttls:
            enable: ${ENABLE_STARTTLS_SMTP}
```

---

## 📚 API Documentation

* OpenAPI / Swagger UI enabled via:

```text
http://localhost:8001/swagger-ui/index.html
```

Includes BearerAuth documentation and request/response schemas.

---

## ⚙️ Configuration (REST)

The application uses `.env` variables (12-Factor style):

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
```

### Main configuration keys

```yaml
server:
  port: ${SERVER_PORT:8001}

spring:
  application:
    name: ${SPRING_APPLICATION_NAME}

  datasource:
    driver-class-name: ${SPRING_DATASOURCE_DRIVER_CLASS_NAME}
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    hikari:
      maximum-pool-size: ${SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE:3}
      minimum-idle: ${SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE:1}
      idle-timeout: ${SPRING_DATASOURCE_HIKARI_IDLE_TIMEOUT:60000}
      max-lifetime: ${SPRING_DATASOURCE_HIKARI_MAX_LIFETIME:180000}
      connection-timeout: ${SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT:10000}

  jpa:
    database-platform: ${SPRING_JPA_DATABASE_PLATFORM:org.hibernate.dialect.MySQLDialect}
    hibernate:
      ddl-auto: ${SPRING_JPA_HIBERNATE_DDL_AUTO:none}
    show-sql: ${SPRING_JPA_SHOW_SQL:true}
    properties:
      hibernate:
        format_sql: ${SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL:true}
        jdbc.time_zone: ${SPRING_JPA_PROPERTIES_HIBERNATE_JDBC_TIME_ZONE:UTC}

jackson:
  time-zone: ${JACKSON_TIMEZONE:America/Bogota}

jwt:
  secret: ${JWT_SECRET}

soap:
  service:
    endpoint: ${SOAP_SERVICE_ENDPOINT}

aws:
  region: ${AWS_REGION}
  s3:
    bucket: ${AWS_NAME_BUCKET}
```

---

## 🗄️ Database

* JPA/Hibernate with clean repository abstraction
* Native queries for performance and conditional updates
* System parameters stored in DB control dynamic runtime behavior (updates, pdf retrieval, email rules, etc.)

---

## 🧪 Testing & Coverage

* JUnit 5 + Mockito
* JaCoCo configured with **minimum 80% line coverage**
* Exclusions typically include:

  * configuration classes
  * DTOs
  * util helpers
  * main application bootstrap class

---

## 🚀 Run Locally (REST)

### Requirements

* Java 17+
* Maven
* MySQL
* AWS credentials/config (if PDF upload is enabled)
* SMTP credentials/config (if email sending is enabled)

### Run

```bash
mvn clean install
mvn spring-boot:run
```

---

## 🐳 Docker Support (REST Microservice)

The REST module includes a **multi-stage Dockerfile** to build and run the service.

### Dockerfile (REST)

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

### Build image

```bash
docker build -t parameta-rest-api .
```

### Run container

```bash
docker run -p 8001:8001 \
  --env-file .env \
  parameta-rest-api
```

---

## ✅ Applied Best Practices

* Clear separation of responsibilities
* Service interfaces + implementations
* Dedicated mappers (MapStruct) + safe mapping patterns
* Centralized exception handling
* Externalized configuration (`.env`)
* Stateless security (JWT)
* DB-driven behavior (validations, email, PDF rules)

---

## 👤 Author

**Brahian Alexander Caceres Guevara**
GitHub: `bcaceres19`
Email: `bacg20044@gmail.com`
