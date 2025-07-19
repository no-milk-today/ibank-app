package com.practice.drm.clients.customer;

import java.time.LocalDate;
import java.util.List;

public record EditUserAccountsRequest(
        String name,
        String email,
        LocalDate birthdate,
        List<String> accounts
) {}
