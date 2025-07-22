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

- All requests pass through the API Gateway on port `8080`.
- **Example:**  
  - **GET** `/api/v1/customers/public`  
    Routes the request to the Customer service.

### Customer Microservice

- **POST** `/api/v1/customers/signup`  
  Registers a new customer using a JSON body with fields: `login`, `password`, `confirmPassword`, `name`, `email`, and `birthdate`.

- **GET** `/api/v1/customers/public`  
  Public endpoint to verify service availability.

- **GET** `/api/v1/customers/main?login={login}`  
  Retrieves main page data for a specific user including account information, available currencies, and user lists.

- **POST** `/api/v1/customers/user/{login}/editPassword`  
  Changes user password. Accepts JSON body with `password` and `confirmPassword` fields.

- **POST** `/api/v1/customers/user/{login}/editUserAccounts`  
  Updates user profile and account settings. Accepts JSON body with `name`, `birthdate`, and `accounts` fields.

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
  Renders the main page using Thymeleaf and loads dynamic user data for the specified user.

- **GET** `/signup`
- Displays the customer registration form.

- **POST** `/signup`  
  Registers a new customer. Accepts form data with `login`, `password`, `confirm_password`, `name`, `email`, and `birthdate` fields.

- **POST** `/user/{login}/editPassword`  
  Processes password change form submission. Accepts form data with `password` and `confirm_password` fields.

- **POST** `/user/{login}/editUserAccounts`  
  Processes user account update form submission. Accepts form data with `name`, `birthdate`, and `accounts` fields.

  
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
4. Access the application (api-gateway) at `http://localhost:8080`.

5. Access the Eureka dashboard at `http://localhost:8761` to see all registered services.

## Usage Examples

### Register a new customer
```bash
POST http://localhost:8080/api/v1/customers/signup
Content-Type: application/json

{
  "login": "john",
  "password": "securePassword123",
  "confirmPassword": "securePassword123", 
  "name": "John Doe",
  "email": "jdoe2@gmail.com",
  "birthdate": "1995-03-15"
}
```

### Change user password
```bash
POST http://localhost:8080/api/v1/customers/user/john/editPassword
Content-Type: application/json

{
  "password": "newPassword123",
  "confirmPassword": "newPassword123"
}
```

### Change user profile and accounts
```bash
POST http://localhost:8080/api/v1/customers/user/john/editUserAccounts
Content-Type: application/json

{
  "name" : "John Doe",
  "birthdate": "1995-03-15",
    "accounts": [
        {
        "currency": "USD",
        "balance": 1000.00
        },
        {
        "currency": "EUR",
        "balance": 500.00
        }
    ]
}
```