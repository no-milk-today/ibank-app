package com.practice.drm.frontui;

import com.practice.drm.frontui.service.KeycloakUserSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final KeycloakUserSyncService userSyncService;

    /**
     * Bean для обработки загрузки OIDC-пользователя во время OAuth2 логина.
     *
     * Использует дефолтный OidcUserService для получения пользователя от провайдера,
     * а затем синхронизирует его с Customer сервисом через KeycloakUserSyncService.
     */
    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            // Загружаем пользователя от Keycloak
            OidcUser oidcUser = delegate.loadUser(userRequest);

            // Синхронизируем с Customer сервисом
            return userSyncService.syncUser(oidcUser);
        };
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // форма регистрации доступна всем
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/signup/**", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                // логин через Keycloak с кастомным user service
                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/keycloak") // 302 на Keycloak
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(oidcUserService()) // используем наш кастомный сервис
                        )
                )
                // выход -> redirect на Keycloak logout
                .logout(logout -> logout
                        .logoutSuccessHandler((req, resp, auth) -> {
                            // remove JSESSIONID and ОIDC-cookie
                            req.getSession(false);
                            // prepare Keycloak logout URL
                            String logoutUri = "http://localhost:8090/realms/bank-realm"
                                    + "/protocol/openid-connect/logout"
                                    + "?post_logout_redirect_uri=http://localhost:8080/";
                            resp.sendRedirect(logoutUri);
                        }))
                // проверяем JWT для внутренних REST-вызовов фронта (если появятся)
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

        return http.build();
    }
}

