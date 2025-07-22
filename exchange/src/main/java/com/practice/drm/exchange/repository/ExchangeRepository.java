package com.practice.drm.exchange.repository;

import com.practice.drm.exchange.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRepository extends JpaRepository<ExchangeRate, Long> {
}
