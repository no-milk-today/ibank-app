package com.practice.drm.exchange.mapper;

import com.practice.drm.clients.customer.Currency;
import com.practice.drm.clients.exchange.ExchangeRateDto;
import com.practice.drm.exchange.model.ExchangeRate;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ExchangeRateFromDTOConverter implements Function<ExchangeRateDto, ExchangeRate> {
    @Override
    public ExchangeRate apply(ExchangeRateDto dto) {
        Currency curr = Currency.valueOf(dto.getName());
        return ExchangeRate.builder()
                .currency(curr)
                .value(dto.getValue())
                .build();
    }
}

