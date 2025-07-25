package com.practice.drm.frontui.service;

import com.practice.drm.clients.customer.CustomerClient;
import com.practice.drm.clients.customer.CustomerRegistrationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakUserSyncService {

    private final CustomerClient customerClient;

    /**
     * Synchronizes the OIDC user with the Customer service.
     *
     * @param oidcUser user from Keycloak
     * @return same OidcUser after synchronization
     */
    public OidcUser syncUser(OidcUser oidcUser) {
        var username = oidcUser.getPreferredUsername();
        var email = oidcUser.getEmail();

        // Получаем полное имя из клейма "name" или через getFullName()
        String fullName = null;
        if (oidcUser.getClaims().containsKey("name")) {
            fullName = oidcUser.getClaim("name");
        } else {
            fullName = oidcUser.getFullName();
        }
        if (fullName == null) {
            fullName = username;
        }

        log.info("Attempt to synchronize User: username={}, email={}, fullName={}",
                 username, email, fullName);

        try {
            // Извлекаем дату рождения из claim "birthdate"
            LocalDate birthdate = null;
            Object b = oidcUser.getClaims().get("birthdate");
            if (b != null) {
                birthdate = LocalDate.parse(b.toString());
            }

            // preparing request for Customer service
            var request = new CustomerRegistrationRequest(
                username,
                null,   // пароль в OAuth2 не нужен
                null,
                fullName,
                email,
                birthdate
            );

            var response = customerClient.registerCustomer(request);

            if (response.success()) {
                log.info("User synchronized successfully: {}", username);
            } else {
                log.warn("User sync returned errors for {}: {}", username, response.errors());
            }

        } catch (Exception ex) {
            log.error("Failed to synchronize user {}: {}", username, ex.getMessage(), ex);
        }

        return oidcUser;
    }
}
