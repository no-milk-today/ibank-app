package com.practice.drm.customer.dto;

public record EditPasswordRequest(
        String password,
        String confirmPassword
) {}
