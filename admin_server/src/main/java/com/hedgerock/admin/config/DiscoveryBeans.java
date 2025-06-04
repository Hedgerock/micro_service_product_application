package com.hedgerock.admin.config;

import com.github.loki4j.client.http.HttpHeader;
import org.springframework.cloud.netflix.eureka.RestTemplateTimeoutProperties;
import org.springframework.cloud.netflix.eureka.http.DefaultEurekaClientHttpRequestFactorySupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.List;

@Configuration
public class DiscoveryBeans {

    @Bean
    public DefaultEurekaClientHttpRequestFactorySupplier defaultEurekaClientHttpRequestFactorySupplier(
            RestTemplateTimeoutProperties restTemplateTimeoutProperties,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService auth2AuthorizedClientService
    ) {

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, auth2AuthorizedClientService
        );

        return new DefaultEurekaClientHttpRequestFactorySupplier(
                restTemplateTimeoutProperties,
                List.of((request, entity, context) -> {
                    if (!request.containsHeader(HttpHeader.AUTHORIZATION)) {

                        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(
                                OAuth2AuthorizeRequest.withClientRegistrationId("discovery")
                                        .principal("admin-service").build()
                        );

                        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                            request.setHeader(HttpHeader.AUTHORIZATION,
                                    "Bearer " + authorizedClient.getAccessToken().getTokenValue());
                        }
                    }
                })
        );
    }

}
