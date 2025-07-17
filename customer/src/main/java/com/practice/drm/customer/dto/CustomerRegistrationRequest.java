package com.practice.drm.customer.dto;

import java.time.LocalDate;

public record CustomerRegistrationRequest(
        String login,
        String password,
        String confirmPassword,
        String name,
        String email,
        LocalDate birthdate) {

}
