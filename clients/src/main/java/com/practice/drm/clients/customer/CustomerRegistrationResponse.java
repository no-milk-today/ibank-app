package com.practice.drm.clients.customer;

import java.util.List;

public record CustomerRegistrationResponse(
        boolean success,
        List<String> errors
) {
}
