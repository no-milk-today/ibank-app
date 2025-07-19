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

- **Notification**  
  Handles sending notifications to customers. It uses a PostgreSQL database for persistence and registers with Eureka. The service receives notification requests from other microservices via Feign clients and processes them asynchronously.

- **API Gateway**  
  Routes incoming requests to the appropriate microservices. It is built using Spring Cloud Gateway and provides load balancing, routing, and fallback capabilities. The gateway integrates with Eureka for service discovery and uses filters to handle cross-cutting concerns like error handling and circuit breaking.

- **FRONT UI**  
  Provides a web user interface using Thymeleaf. The service runs on port `8085` and integrates with other microservices via Feign clients. It exposes endpoints such as `/` for the landing page (redirecting to `/main`) and `/main?login={login}` to render the main page with dynamic user data.
  
## Technologies

- Spring Boot
- Spring Cloud (Eureka, OpenFeign, Load Balancer)
- Netflix OSS
- Resilience4j (Circuit breaker and Retry)
- Maven

## Service Endpoints

### API Gateway

- All requests pass through the API Gateway.
- **Example:**  
  - **GET** `/api/v1/customers/public`  
    Routes the request to the Customer service.

### Customer Microservice

- **POST** `/api/v1/customers`  
  Registers a new customer using a JSON body with fields: \`firstName\`, \`lastName\`, and \`email\`.

- **GET** `/api/v1/customers/public`  
  Public endpoint to verify service availability.

### Fraud Microservice

- **GET** `/api/v1/fraud-check/{customerId}`  
  Checks if the customer with the provided \`customerId\` is fraudulent. Stores the fraud check history.

### Notification Microservice

- **POST** `/api/v1/notifications`  
  Sends a notification to a customer.
- The service is configured to run on port \`8083\` and uses PostgreSQL for data storage.

### Clients Module

- Provides Feign clients for inter-service communication such as contacting the Fraud and Notification services.
- The Feign clients are defined to resolve service names (e.g. \`http://fraud\`, \`http://notification\`) and load balance using Spring Cloud LoadBalancer.

### FRONT UI Microservice

- **GET** `/`  
  Redirects to the main page.
- **GET** `/main?login={login}`  
  Renders the main page using Thymeleaf and loads dynamic user data.
  
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
5. Access the application (api-gateway) at `http://localhost:8084`.

6. Access the Eureka dashboard at `http://localhost:8761` to see all registered services.
