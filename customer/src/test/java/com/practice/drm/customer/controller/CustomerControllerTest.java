package com.practice.drm.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.drm.clients.customer.*;
import com.practice.drm.customer.config.SecurityConfig;
import com.practice.drm.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
@Import(SecurityConfig.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private CustomerService customerService;

    private Jwt testJwt;

    @BeforeEach
    void setupJwtDecoder() {
        // минимальный Jwt с нужным claim preferred_username
        testJwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("preferred_username", "john")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(jwtDecoder.decode(anyString())).thenReturn(testJwt);
    }

    @Test
    @DisplayName("POST /api/v1/customers/signup returns JSON registration response")
    void registerCustomer_returnsJson() throws Exception {
        var req = new CustomerRegistrationRequest(
                "john", "pass", "pass", "John Doe", "john@example.com", LocalDate.of(1990, 1, 1)
        );
        var resp = new CustomerRegistrationResponse(true, List.of());
        when(customerService.registerCustomer(any())).thenReturn(resp);

        mockMvc.perform(post("/api/v1/customers/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));

        verify(customerService).registerCustomer(req);
    }

    @Test
    @DisplayName("GET /api/v1/customers/public is accessible without auth")
    void publicEndpoint_withoutJwt() throws Exception {
        mockMvc.perform(get("/api/v1/customers/public"))
                .andExpect(status().isOk())
                .andExpect(content().string("Customers public endpoint"));
    }

    @Test
    @DisplayName("GET /api/v1/customers/main requires auth and invokes service")
    void main_requiresJwt_andInvokesService() throws Exception {
        var mainData = new MainPageData(
                "john", "John Doe", "john@example.com",
                LocalDate.of(1990, 1, 1),
                List.of(), List.of(), List.of(), null, null, null, null, null
        );
        when(customerService.getMainData("john")).thenReturn(mainData);

        mockMvc.perform(get("/api/v1/customers/main")
                        .with(jwt().jwt(jwt -> jwt.claim("preferred_username", "john")))
                        .param("login", "john")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("john"))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(customerService).getMainData("john");
    }

    @Test
    @DisplayName("PUT /api/v1/customers/{login}/accounts/{currency}/balance updates balance")
    void updateAccountBalance_requiresJwt() throws Exception {
        BigDecimal newBal = BigDecimal.valueOf(500);

        mockMvc.perform(put("/api/v1/customers/john/accounts/USD/balance")
                        .with(jwt().jwt(jwt -> jwt.claim("preferred_username", "john")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(newBal)))
                .andExpect(status().isOk());

        verify(customerService).updateAccountBalance("john", "USD", newBal);
    }
}
