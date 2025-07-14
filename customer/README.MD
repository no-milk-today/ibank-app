# Customer Microservice

This microservice handles customer registration and related endpoints using Spring Boot, Maven, and Resilience4j for circuit breaking.

## Features

- Customer registration endpoint with fraud check integration
- REST API endpoints for customer actions
- Circuit Breaker implementation using Resilience4j
- Load-balanced REST client with Spring Cloud

## Endpoints

- **POST** `/api/v1/customers`  
  Registers a new customer using a JSON body with fields: `firstName`, `lastName`, and `email`.

- **GET** `/api/v1/customers/public`  
  Public endpoint to verify service availability.

## Running the Application

1. Ensure you have Java and Maven installed.
2. Build the project:
   ```bash
   mvn clean install
    ```
3. Run the application:
4. ```bash
   mvn spring-boot:run
   ```
5. Access the application at `http://localhost:8081`.

## Configuration
The REST template is configured in `customer/src/main/java/com/practice/drm/customer/CustomerConfig.java` with client-side load balancing.
Circuit breaker settings can be customized in the `application.yml` if needed.
## Dependencies
* Spring Boot
* Spring Cloud (LoadBalanced REST Template)
* Resilience4j (Circuit breaker and Retry)
* Spring Web
* Lombok (for logging, DTOs, etc.)

