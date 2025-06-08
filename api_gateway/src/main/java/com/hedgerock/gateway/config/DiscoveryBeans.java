package com.hedgerock.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.eureka.RestTemplateTimeoutProperties;
import org.springframework.cloud.netflix.eureka.http.DefaultEurekaClientHttpRequestFactorySupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;

import java.util.List;

@Configuration
@ConditionalOnProperty(name = "eureka.client.enabled", havingValue = "true", matchIfMissing = true)
public class DiscoveryBeans {

    @Bean
    public DefaultEurekaClientHttpRequestFactorySupplier defaultEurekaClientHttpRequestFactorySupplier(
            RestTemplateTimeoutProperties templateTimeoutProperties,
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService auth2AuthorizedClientService
    ) {
        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, auth2AuthorizedClientService);

        return new DefaultEurekaClientHttpRequestFactorySupplier(templateTimeoutProperties,
                List.of((request, entity, context) -> {
                    if (!request.containsHeader(HttpHeaders.AUTHORIZATION)) {

                        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(
                                OAuth2AuthorizeRequest
                                        .withClientRegistrationId("discovery")
                                        .principal("api-gateway")
                                        .build()).block();

                        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                            request.setHeader(HttpHeaders.AUTHORIZATION,
                                    "Bearer " + authorizedClient.getAccessToken().getTokenValue());
                        }
                    }
                })
        );
    }

}
