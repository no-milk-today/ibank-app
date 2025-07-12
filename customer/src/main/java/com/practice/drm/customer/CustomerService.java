package com.practice.drm.customer;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;

    public CustomerService(CustomerRepository customerRepository, RestTemplate restTemplate) {
        this.customerRepository = customerRepository;
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "fraudCheckService", fallbackMethod = "fraudCheckFallback")
    @Retry(name = "fraudCheckService")
    public void registerCustomer(CustomerRegistrationRequest request) {
        var customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        // todo: check if email valid
        // todo: check if not taken

        // If we don’t say ‘save and FLUSH’ then the ID will be null.
        customerRepository.saveAndFlush(customer);

        var fraudCheckResponse = restTemplate.getForObject(
                "http://FRAUD/api/v1/fraud-check/{customerId}",
                FraudCheckResponse.class,
                customer.getId()
        );
        customerRepository.save(customer);
        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }
        // todo: send a notification
    }

    public void fraudCheckFallback(CustomerRegistrationRequest request, Throwable ex) {
        log.error("Fraud check service is unavailable. Fallback method invoked for request: {}", request, ex);
        // Логика fallback: например, логирование, сохранение статуса, отправка уведомления
        throw new IllegalStateException("Fraud service unavailable. Please try later.", ex);
    }
}
