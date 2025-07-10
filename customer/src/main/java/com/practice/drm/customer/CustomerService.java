package com.practice.drm.customer;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public record CustomerService(
        CustomerRepository customerRepository,
        RestTemplate restTemplate
) {
    public void registerCustomer(CustomerRegistrationRequest request) {
        var customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .firstName(request.email())
                .build();
        // todo: check if email valid
        // todo: check if not taken

        // If we don’t say ‘save and FLUSH’ then the ID will be null.
        customerRepository.saveAndFlush(customer);

        var fraudCheckResponse = restTemplate.getForObject(
                "http://localhost:8082/api/v1/fraud-check/{customerId}",
                FraudCheckResponse.class,
                customer.getId()
        );
        customerRepository.save(customer);
        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }
        // todo: send a notification
    }
}
