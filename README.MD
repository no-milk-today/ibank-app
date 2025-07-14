# Bank Microservices Project

This project is composed of multiple microservices using the Netflix stack (Eureka, Feign, Resilience4j) with Spring Boot and Maven.

## Modules

- **Customer**  
  Handles customer registration and related actions. It uses Resilience4j circuit breaker and retry functionalities. The service communicates with the Fraud service using Feign clients.

- **Fraud**  
  Provides endpoints to check if a customer is fraudulent. It stores fraud check history and uses a service to determine fraud status.

- **Eureka Server**  
  Acts as a service discovery server for all microservices.

- **Clients**  
  Contains shared Feign clients and response object definitions used for inter-service communication.

## Technologies
- Spring Boot
- Spring Cloud (Eureka, OpenFeign)
- Netflix OSS
- Resilience4j (Circuit breaker and Retry)
- Maven

## Service Endpoints

### Customer Microservice

- **POST** `/api/v1/customers`  
  Registers a new customer using a JSON body with fields: \`firstName\`, \`lastName\`, and \`email\`.

- **GET** `/api/v1/customers/public`  
  Public endpoint to verify service availability.

### Fraud Microservice

- **GET** `/api/v1/fraud-check/{customerId}`  
  Checks if the customer with the provided \`customerId\` is fraudulent. Stores the fraud check history.

### Clients Module

- Provides a Feign client to communicate with the Fraud service.
- The client is defined to resolve the service name (e.g. using \`http://fraud\`), and load balances using Spring Cloud LoadBalancer.
- It also includes response objects needed for the service communication.

## Running the Project

1. Ensure you have Java and Maven installed.
2. Build the project:
   ```bash
   mvn clean install
    ```
3. Run the services (for example, start the Eureka Server first):
   ```bash
   mvn spring-boot:run
    ```
5. // todo: Access the application at `http://localhost:8080`. Not implemented yet.

