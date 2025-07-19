package com.practice.drm.clients.customer;

public record EditPasswordRequest(
        String password,
        String confirmPassword
) {}
