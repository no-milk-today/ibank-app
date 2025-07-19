package com.practice.drm.frontui;

import com.practice.drm.clients.customer.CustomerClient;
import com.practice.drm.clients.customer.MainPageData;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FrontUiService {

    private final CustomerClient customerClient;

    @CircuitBreaker(name = "customer-client", fallbackMethod = "getMainPageDataFallback")
    @Retry(name = "customer-client")
    public MainPageData getMainPageData(String login) {
    log.info("Fetching main page data for user: {}", login);
        return customerClient.getMainPage(login);
    }

    public MainPageData getMainPageDataFallback(String login, Exception ex) {
        log.error("Customer service unavailable for user: {}. Error: {}", login, ex.getMessage());
        return new MainPageData(
            login,
            "Пользователь",
            "",
            null,
            List.of(),
            List.of(),
            List.of(),
            List.of("Сервис аккаунтов временно недоступен. Попробуйте позже."),
            null,
            null,
            null,
            null
        );
    }
}
