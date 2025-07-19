package com.practice.drm.clients.customer;

import java.time.LocalDate;

public record CustomerRegistrationRequest(
        String login,
        String password,
        String confirmPassword,
        String name,
        String email,
        LocalDate birthdate) {

}
