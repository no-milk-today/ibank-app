package com.practice.drm.customer;

import com.practice.drm.clients.customer.EditPasswordRequest;
import com.practice.drm.clients.customer.MainPageData;
import com.practice.drm.customer.dto.CustomerRegistrationRequest;
import com.practice.drm.customer.dto.CustomerRegistrationResponse;
import com.practice.drm.clients.customer.EditUserAccountsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
            @PathVariable String login,
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
}
