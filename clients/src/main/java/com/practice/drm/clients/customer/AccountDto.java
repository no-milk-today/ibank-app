package com.practice.drm.clients.customer;

import java.math.BigDecimal;

public record AccountDto(
        Currency currency,
        String currencyCode,
        String currencyTitle,
        BigDecimal balance,
        boolean exists
) {}
