package com.practice.drm.customer.repository;

import com.practice.drm.clients.customer.Currency;
import com.practice.drm.customer.model.Account;
import com.practice.drm.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findAllByCustomer(Customer customer);
    boolean existsByCustomerAndCurrency(Customer customer, Currency currency);
}
