package com.practice.drm.frontui;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return template -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof OAuth2AuthenticationToken oauth2Token) {
                OAuth2AuthorizedClient client = authorizedClientService
                        .loadAuthorizedClient(
                                oauth2Token.getAuthorizedClientRegistrationId(),
                                oauth2Token.getName()
                        );
                if (client != null && client.getAccessToken() != null) {
                    template.header(
                            "Authorization",
                            "Bearer " + client.getAccessToken().getTokenValue()
                    );
                }
            }
        };
    }
}
