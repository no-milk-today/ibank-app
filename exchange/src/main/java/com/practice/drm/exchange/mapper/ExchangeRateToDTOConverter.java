package com.practice.drm.exchange.mapper;

import com.practice.drm.clients.exchange.ExchangeRateDto;
import com.practice.drm.exchange.model.ExchangeRate;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ExchangeRateToDTOConverter implements Function<ExchangeRate, ExchangeRateDto> {
    @Override
    public ExchangeRateDto apply(ExchangeRate entity) {
        return new ExchangeRateDto(
                entity.getCurrency().getTitle(),
                entity.getCurrency().name(),
                entity.getValue()
        );
    }
}

