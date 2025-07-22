package com.practice.drm.exchange.mapper;

import com.practice.drm.clients.customer.Currency;
import com.practice.drm.clients.exchange.ExchangeRateDto;
import com.practice.drm.exchange.model.ExchangeRate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExchangeRateConvertersTest {

    private final ExchangeRateToDTOConverter toDto = new ExchangeRateToDTOConverter();
    private final ExchangeRateFromDTOConverter fromDto = new ExchangeRateFromDTOConverter();

    @Test
    void toDto_convertsEntityToDto() {
        var entity = ExchangeRate.builder()
                .currency(Currency.USD)
                .value(93.5)
                .build();

        var dto = toDto.apply(entity);

        assertThat(dto.getTitle()).isEqualTo(Currency.USD.getTitle());
        assertThat(dto.getName()).isEqualTo("USD");
        assertThat(dto.getValue()).isEqualTo(93.5);
    }

    @Test
    void fromDto_convertsDtoToEntity() {
        var dto = new ExchangeRateDto("Юань","CNY",13.2);

        var entity = fromDto.apply(dto);

        assertThat(entity.getCurrency()).isEqualTo(Currency.CNY);
        assertThat(entity.getValue()).isEqualTo(13.2);
    }
}
