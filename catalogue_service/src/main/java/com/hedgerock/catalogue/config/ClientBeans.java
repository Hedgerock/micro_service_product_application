package com.hedgerock.catalogue.config;

import de.codecentric.boot.admin.client.registration.BlockingRegistrationClient;
import de.codecentric.boot.admin.client.registration.RegistrationClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class ClientBeans {

    @Bean
    public RegistrationClient registrationClient(
            ClientRegistrationRepository registrationRepository,
            OAuth2AuthorizedClientService clientService
    ) {
        var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrationRepository, clientService);
        var restTemplate = new RestTemplateBuilder()
                .interceptors((request, body, execution) -> {
                    if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                        var authorizedClient = authorizedClientManager.authorize(OAuth2AuthorizeRequest
                                .withClientRegistrationId("keycloak")
                                .principal("catalogue-service-metrics-client")
                                .build()
                        );

                        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                            request.getHeaders().setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
                        }
                    }

                    return execution.execute(request, body);
                })
                .build();

        return new BlockingRegistrationClient(restTemplate);
    }

}
