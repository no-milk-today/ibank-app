package com.practice.drm.customer;

import com.practice.drm.clients.fraud.FraudClient;
import com.practice.drm.clients.notification.NotificationClient;
import com.practice.drm.clients.notification.NotificationRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;


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

        var fraudCheckResponse =
                fraudClient.isFraudster(customer.getId());

        customerRepository.save(customer);
        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }
        // todo: make it async. i.e. add to queue
        notificationClient.sendNotification(
                new NotificationRequest(
                        customer.getId(),
                        customer.getEmail(),
                        String.format("Hi %s, welcome to Bank-system...",
                                customer.getFirstName())
                )
        );
    }

    public void fraudCheckFallback(CustomerRegistrationRequest request, Throwable ex) {
        log.error("Fraud check service is unavailable. Fallback method invoked for request: {}", request, ex);
        // Логика fallback: например, логирование, сохранение статуса, отправка уведомления
        throw new IllegalStateException("Fraud service unavailable. Please try later.", ex);
    }
}
