package com.practice.drm.customer.controller;

import com.practice.drm.clients.customer.*;
import com.practice.drm.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/signup")
    public CustomerRegistrationResponse registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest) {
        log.info("new customer registration {}", customerRegistrationRequest);
        return customerService.registerCustomer(customerRegistrationRequest);
    }

    @GetMapping("/public")
    public String getCustomer() {
        return "Customers public endpoint";
    }

    @GetMapping("/main")
    public MainPageData main(@RequestParam("login") String login) {
        return customerService.getMainData(login);
    }

    @PostMapping("/user/{login}/editUserAccounts")
    public List<String> editUserAccounts(
            @PathVariable("login") String login,
            @RequestBody EditUserAccountsRequest req
    ) {
        return customerService.editUserProfile(login, req);
    }

    @PostMapping("/user/{login}/editPassword")
    public List<String> editPassword(
            @PathVariable("login") String login,
            @RequestBody EditPasswordRequest req
    ) {
        return customerService.editPassword(login, req);
    }

    @GetMapping("/{login}")
    public CustomerDto getCustomer(@PathVariable("login") String login) {
        return customerService.getCustomerByLogin(login);
    }

    @PutMapping("/{login}/accounts/{currency}/balance")
    public void updateAccountBalance(
            @PathVariable("login") String login,
            @PathVariable("currency") String currency,
            @RequestBody BigDecimal newBalance) {

        log.info("Updating account balance for user {}: {} {} = {}", login, currency, "new balance", newBalance);
        customerService.updateAccountBalance(login, currency, newBalance);
    }
}
