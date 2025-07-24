package com.practice.drm.frontui;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // форма регистрации доступна всем
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/signup/**", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                // логин через Keycloak
                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/keycloak") // 302 на Keycloak
                )
                // выход -> redirect на Keycloak logout
                .logout(logout -> logout
                        .logoutSuccessUrl("http://localhost:8090/realms/bank-realm/protocol/openid-connect/logout?redirect_uri=http://localhost:8085/")
                )
                // проверяем JWT для внутренних REST-вызовов фронта (если появятся)
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

        return http.build();
    }
}

