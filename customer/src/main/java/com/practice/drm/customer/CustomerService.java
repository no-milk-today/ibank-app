package com.practice.drm.customer;

import org.springframework.stereotype.Service;

@Service
public record CustomerService(CustomerRepository customerRepository) {
    public void registerCustomer(CustomerRegistrationRequest request) {
        var customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .firstName(request.email())
                .build();
        // todo: check if email valid
        // todo: check if not taken
        customerRepository.save(customer);
    }
}
