package com.practice.drm.customer.dto;

import com.practice.drm.clients.customer.AccountDto;

import java.time.LocalDate;
import java.util.List;

public record CustomerDto(
        String login,
        String name,
        String email,
        LocalDate birthdate,
        List<AccountDto> accounts
) {}
