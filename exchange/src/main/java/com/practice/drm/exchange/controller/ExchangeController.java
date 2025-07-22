package com.practice.drm.exchange.controller;

import com.practice.drm.clients.exchange.ExchangeRateDto;
import com.practice.drm.exchange.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rates")
@RequiredArgsConstructor
public class ExchangeController {
    private final ExchangeService exchangeService;

    @GetMapping
    public List<ExchangeRateDto> getRates() {
        return exchangeService.getAllRates();
    }

    // it is used by exchange-generator service to update exchange rates via REST API
    @PutMapping("/update")
    public void updateRates(@RequestBody List<ExchangeRateDto> rates) {
        exchangeService.updateRates(rates);
    }
}

