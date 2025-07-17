package com.practice.drm.customer.dto;

import java.util.List;

public record CustomerRegistrationResponse(
        boolean success,
        List<String> errors
) {
}
