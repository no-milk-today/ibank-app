package com.practice.drm.customer.dto;

import com.practice.drm.customer.Currency;
import java.math.BigDecimal;

public record AccountDto(
        Currency currency,
        String currencyCode,
        String currencyTitle,
        BigDecimal balance,
        boolean exists
) {}
