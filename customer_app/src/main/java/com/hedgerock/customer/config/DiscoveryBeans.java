package com.hedgerock.customer.config;

import com.github.loki4j.client.http.HttpHeader;
import org.springframework.cloud.netflix.eureka.RestTemplateTimeoutProperties;
import org.springframework.cloud.netflix.eureka.http.DefaultEurekaClientHttpRequestFactorySupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;

import java.util.List;

@Configuration
public class DiscoveryBeans {

    @Bean
    public DefaultEurekaClientHttpRequestFactorySupplier defaultEurekaClientHttpRequestFactorySupplier(
            RestTemplateTimeoutProperties  restTemplateTimeoutProperties,
            ReactiveClientRegistrationRepository registrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService
    ) {
        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager clientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(registrationRepository, authorizedClientService);

        return new DefaultEurekaClientHttpRequestFactorySupplier(
                restTemplateTimeoutProperties,
                List.of((request, entity, context) -> {
                    if (!request.containsHeader(HttpHeader.AUTHORIZATION)) {

                        OAuth2AuthorizedClient authorizedClient = clientManager.authorize(
                                OAuth2AuthorizeRequest
                                        .withClientRegistrationId("discovery")
                                        .principal("customer-application-metrics-client")
                                        .build()
                        ).block();


                        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                            request.setHeader(HttpHeader.AUTHORIZATION,
                                    "Bearer " + authorizedClient.getAccessToken().getTokenValue());
                        }
                    }
                })
        );

    }

}
