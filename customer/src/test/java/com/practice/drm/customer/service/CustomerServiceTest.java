package com.practice.drm.customer.service;

import com.practice.drm.clients.customer.Currency;
import com.practice.drm.clients.customer.CustomerRegistrationRequest;
import com.practice.drm.clients.customer.EditPasswordRequest;
import com.practice.drm.clients.fraud.FraudCheckResponse;
import com.practice.drm.clients.fraud.FraudClient;
import com.practice.drm.clients.notification.NotificationClient;
import com.practice.drm.clients.notification.NotificationRequest;
import com.practice.drm.customer.model.Account;
import com.practice.drm.customer.repository.AccountRepository;
import com.practice.drm.customer.model.Customer;
import com.practice.drm.customer.repository.CustomerRepository;
import com.practice.drm.customer.exception.AccountNotFoundException;
import com.practice.drm.customer.exception.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private FraudClient fraudClient;
    @Mock
    private NotificationClient notificationClient;
    @InjectMocks
    private CustomerService customerService;

    @Test
    void registerCustomer() {
        var req = new CustomerRegistrationRequest(
                "login1", "pass", "pass", "John Doe", "john@example.com", LocalDate.of(2000, 1, 1)
        );

        when(customerRepository.existsByLogin("login1")).thenReturn(false);
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(fraudClient.isFraudster(anyInt())).thenReturn(new FraudCheckResponse(false));

        var savedCustomer = Customer.builder()
                .id(123)
                .login("login1")
                .name("John Doe")
                .email("john@example.com")
                .birthdate(LocalDate.of(2000, 1, 1))
                .accounts(Collections.emptyList())
                .build();

        when(customerRepository.saveAndFlush(any())).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(123); // simulate ID assignment
            return c;
        });

        var result = customerService.registerCustomer(req);

        assertThat(result.success()).isTrue();
        verify(notificationClient).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void registerCustomer_validationErrors() {
        var req = new CustomerRegistrationRequest(
                "", "", "mismatch", "", "invalid", LocalDate.of(2020, 1, 1)
        );
        var result = customerService.registerCustomer(req);

        assertFalse(result.success());
        assertThat(result.errors()).isNotEmpty();
    }

    @Test
    void registerCustomer_isFraudster() {
        var req = new CustomerRegistrationRequest(
                "login2", "pass", "pass", "Hacker", "evil@example.com", LocalDate.of(1990, 1, 1)
        );

        when(customerRepository.existsByLogin("login2")).thenReturn(false);
        when(customerRepository.existsByEmail("evil@example.com")).thenReturn(false);
        when(fraudClient.isFraudster(anyInt())).thenReturn(new FraudCheckResponse(true));

        var customer = Customer.builder().login("login2").email("evil@example.com").build();
        when(customerRepository.saveAndFlush(any())).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(999);
            return c;
        });

        var response = customerService.registerCustomer(req);

        assertThat(response.success()).isFalse();
        assertThat(response.errors()).contains("Your registration is blocked due to fraud suspicion");
        verify(customerRepository).delete(any());
    }

    @Test
    void updateAccountBalance() {
        var customer = new Customer();
        customer.setLogin("bob");
        var account = new Account();
        account.setCurrency(Currency.RUB);
        account.setBalance(BigDecimal.valueOf(500));
        customer.setAccounts(List.of(account));

        when(customerRepository.findByLogin("bob")).thenReturn(Optional.of(customer));

        customerService.updateAccountBalance("bob", "RUB", BigDecimal.valueOf(800));

        assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(800));
        verify(customerRepository).save(customer);
    }

    @Test
    void updateAccountBalance_accountNotFound() {
        var customer = Customer.builder()
                .login("bob")
                .accounts(List.of())
                .build();

        when(customerRepository.findByLogin("bob")).thenReturn(Optional.of(customer));

        assertThatThrownBy(() ->
                customerService.updateAccountBalance("bob", "USD", BigDecimal.TEN))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void getCustomerByLogin_notFound() {
        when(customerRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerByLogin("unknown"))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void editPassword() {
        var customer = new Customer();
        customer.setLogin("alice");

        when(customerRepository.findByLogin("alice")).thenReturn(Optional.of(customer));

        var req = new EditPasswordRequest("newPass", "newPass");

        var result = customerService.editPassword("alice", req);

        assertThat(result).isEmpty();
        assertThat(customer.getPasswordHash()).isNotEmpty();
        assertThat(BCrypt.checkpw("newPass", customer.getPasswordHash())).isTrue();
    }

    @Test
    void editPassword_invalid() {
        var req = new EditPasswordRequest("pass1", "pass2");
        var errors = customerService.editPassword("user", req);
        assertThat(errors).contains("Passwords do not match");
    }

}