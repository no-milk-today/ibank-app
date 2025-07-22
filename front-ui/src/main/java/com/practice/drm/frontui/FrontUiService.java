package com.practice.drm.frontui;

import com.practice.drm.clients.customer.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public List<String> changePassword(String login, String password, String confirmPassword) {
        return customerClient.editPassword(login, new EditPasswordRequest(password, confirmPassword));
    }

    @CircuitBreaker(name = "customer-client", fallbackMethod = "registerCustomerFallback")
    @Retry(name = "customer-client")
    public CustomerRegistrationResponse registerCustomer(
            String login,
            String password,
            String confirmPassword,
            String name,
            String email,
            LocalDate birthdate
    ) {
        log.info("Registering new customer: {}", login);
        var request = new CustomerRegistrationRequest(
                login, password, confirmPassword, name, email, birthdate
        );
        return customerClient.registerCustomer(request);
    }

    public CustomerRegistrationResponse registerCustomerFallback(
            String login, String password, String confirmPassword,
            String name, String email, LocalDate birthdate, Exception ex
    ) {
        log.error("Customer registration service unavailable for user: {}. Error: {}", login, ex.getMessage());
        return new CustomerRegistrationResponse(
                false,
                List.of("Сервис регистрации временно недоступен. Попробуйте позже.")
        );
    }

    @CircuitBreaker(name = "customer-client", fallbackMethod = "editUserAccountsFallback")
    @Retry(name = "customer-client")
    public List<String> editUserAccounts(
            String login,
            String name,
            LocalDate birthdate,
            List<String> accounts
    ) {
        log.info("Editing accounts for customer: {}", login);
        var request = new EditUserAccountsRequest(name, birthdate, accounts);
        return customerClient.editUserAccounts(login, request);
    }

    public List<String> editUserAccountsFallback(
            String login, String name,
            LocalDate birthdate, List<String> accounts, Exception ex
    ) {
        log.error("Customer edit accounts service unavailable for user: {}. Error: {}", login, ex.getMessage());
        return List.of("Сервис редактирования профиля временно недоступен. Попробуйте позже.");
    }
}
