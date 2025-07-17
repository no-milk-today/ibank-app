package com.practice.drm.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findAllByCustomer(Customer customer);
    boolean existsByCustomerAndCurrency(Customer customer, Currency currency);
}
