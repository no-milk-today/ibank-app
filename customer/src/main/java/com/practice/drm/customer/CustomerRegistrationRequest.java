package com.practice.drm.customer;

public record CustomerRegistrationRequest(
        String firstName,
        String lastName,
        String email) {

}
