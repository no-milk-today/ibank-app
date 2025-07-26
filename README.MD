# Bank Microservices Project

This project is composed of multiple microservices using the Netflix stack (Eureka, Feign, Resilience4j) with Spring Boot and Maven.

## Modules

- **Customer**  
  Handles customer registration and related actions. It uses Resilience4j circuit breaker and retry functionalities. The service communicates with the Fraud service using Feign clients.

- **Fraud**  
  Provides endpoints to check if a customer is fraudulent. It stores fraud check history and uses a service to determine fraud status.

- **Exchange**  
  Manages currency exchange rates for the banking system. Provides real-time currency rates relative to RUB (base currency) and handles rate updates. Supports RUB, USD, and CNY currencies. The service stores exchange rates in PostgreSQL and integrates with the Exchange Generator service for automatic rate updates.

- **Exchange Generator**  
  A separate service that provides exchange rates for RUB, USD, and CNY. Updates the Exchange service with new rates every 5 seconds via Feign Client. It uses JSON file with exchange rates as a data source (using Round Robin strategy for rate selection).

- **Eureka Server**  
  Acts as a service discovery server for all microservices.

- **Clients**  
  Contains shared Feign clients and response object definitions used for inter-service communication.

- **Notification**  
  Handles sending notifications to customers. It uses a PostgreSQL database for persistence and registers with Eureka. The service receives notification requests from other microservices via Feign clients and processes them asynchronously.

- **API Gateway**  
  Routes incoming requests to the appropriate microservices. It is built using Spring Cloud Gateway and provides load balancing, routing, and fallback capabilities. The gateway integrates with Eureka for service discovery and uses filters to handle cross-cutting concerns like error handling and circuit breaking.

- **FRONT UI**  
  Provides a web user interface using Thymeleaf. The service runs on port `8085` and integrates with other microservices via Feign clients. It exposes endpoints such as `/` for the landing page (redirecting to `/main`) and `/main?login={login}` to render the main page with dynamic user data. The UI automatically updates exchange rates every second using JavaScript.
  
## Technologies

- Spring Boot
- Spring Cloud (Eureka, OpenFeign, Load Balancer)
- Netflix OSS
- Resilience4j (Circuit breaker and Retry)
- PostgreSQL
- Maven

## Security & Authentication

### OAuth2-Login через Keycloak
- Использован **Keycloak** как OAuth2 Authorization Server (порт `8090`, realm `bank-realm`).
- Front-UI (`front-ui`) настроен на **Authorization Code Flow**:
  - public client `front-ui`
  - redirect URI: `http://localhost:8085/login/oauth2/code/keycloak`
- API Gateway (`apigateway`) и все микросервисы (`customer`, `exchange`, `notification`, `fraud`) настроены как **resource-servers**:
  - проверяют JWT от Keycloak (issuer-uri `http://localhost:8090/realms/bank-realm`).
  - межсервисный вызов защищён через **client-credentials** (confidential clients c сервис-аккаунтами).
- При входе пользователь попадает на IDP, получает JWT, который Gateway проксирует во внутренние микросервисы (TokenRelay).
- При выходе Front-UI делает SSO-logout в Keycloak и перенаправляет обратно на корень приложения.
### Just-In-Time Provisioning
- При первом логине пользователя через Keycloak Front-UI автоматически синхронизирует профиль:
  1. OAuth2-flow завершён → Spring Security получает `OidcUser`.
  2. `KeycloakUserSyncService` подслушивает `AuthenticationSuccessEvent` и вызывает CustomerClient для создания/обновления профиля в Customer-сервисе.
- При регистрации через Front-UI форма `/signup`:
  1. создаёт пользователя в Keycloak Admin API (`front-ui-admin` client, роль `manage-users`);
  2. затем создаёт профиль в Customer-сервисе;
  3. редиректит на стандартный OAuth2-login (`/oauth2/authorization/keycloak`), и пользователь сразу входит в систему.

### Дальнейшее использование
- Все защищённые REST-эндпоинты (`/api/v1/customers/**` и т.д.) требуют валидный JWT.
- Публичные пути: `/signup/**`, статика, `/api/rates/**` (для динамического JS-запроса курсов без токена).


## Service Endpoints

### API Gateway

- All requests pass through the API Gateway on port `8080`.
- **Example:**  
  - **GET** `/api/v1/customers/public`  
    Routes the request to the Customer service.
  - **GET** `/api/rates`  
    Routes the request to the Exchange service for currency rates.

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

### Exchange Microservice

- **GET** `/api/rates`  
  Retrieves current currency exchange rates. Returns a JSON array with exchange rate information for all supported currencies (RUB, USD, CNY).

- **PUT** `/api/rates/update`  
  Updates currency exchange rates. Accepts a JSON array of exchange rate objects with `title`, `name`, and `value` fields.

### Fraud Microservice

- **GET** `/api/v1/fraud-check/{customerId}`  
  Checks if the customer with the provided \`customerId\` is fraudulent. Stores the fraud check history.

### Notification Microservice

- **POST** `/api/v1/notifications`  
  Sends a notification to a customer.
- The service is configured to run on port \`8083\` and uses PostgreSQL for data storage.

### Clients Module

- Provides Feign clients for inter-service communication such as contacting the Fraud, Notification, and Exchange services.
- The Feign clients are defined to resolve service names (e.g. `http://fraud`, `http://notification`, `http://exchange`) and load balance using Spring Cloud LoadBalancer.

### FRONT UI Microservice

- **GET** `/`  
  Redirects to the main page.
  
- **GET** `/main?login={login}`  
  Renders the main page using Thymeleaf and loads dynamic user data for the specified user. Automatically fetches and displays current exchange rates.

- **GET** `/signup`  
  Displays the customer registration form.

- **POST** `/signup`  
  Registers a new customer. Accepts form data with `login`, `password`, `confirm_password`, `name`, `email`, and `birthdate` fields.

- **POST** `/user/{login}/editPassword`  
  Processes password change form submission. Accepts form data with `password` and `confirm_password` fields.

- **POST** `/user/{login}/editUserAccounts`  
  Processes user account update form submission. Accepts form data with `name`, `birthdate`, and `accounts` fields.

## Architecture Overview

The system consists of the following microservices running on different ports:

- **Eureka Server**: `8761` - Service discovery
- **Customer**: `8081` - User management and accounts
- **Fraud**: `8082` - Fraud detection
- **Notification**: `8083` - Notification handling  
- **API Gateway**: `8080` - Request routing and load balancing
- **Front UI**: `8085` - Web user interface
- **Exchange**: `8086` - Currency exchange rates management
- **Exchange Generator**: `8087` - Automatic exchange rate generation

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
    "RUB",
    "USD"
  ]
}
```

### Get current exchange rates
```bash
GET http://localhost:8080/api/rates
Accept: application/json
```

**Response:**
```json
[
  {
    "title": "Рубль",
    "name": "RUB", 
    "value": 1.0
  },
  {
    "title": "Доллар",
    "name": "USD",
    "value": 93.50
  },
  {
    "title": "Юань", 
    "name": "CNY",
    "value": 13.20
  }
]
```

### Update exchange rates
```bash
PUT http://localhost:8086/api/rates/update
Content-Type: application/json

[
  {
    "title": "Рубль",
    "name": "RUB",
    "value": 1.0
  },
  {
    "title": "Доллар",
    "name": "USD",
    "value": 94.00
  },
  {
    "title": "Юань",
    "name": "CNY",
    "value": 13.50
  }
]
```