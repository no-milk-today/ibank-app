package com.practice.drm.clients.transfer;

import java.math.BigDecimal;

public record TransferRequest(
        String fromCurrency,
        String toCurrency,
        BigDecimal value,
        String toLogin
) { }
