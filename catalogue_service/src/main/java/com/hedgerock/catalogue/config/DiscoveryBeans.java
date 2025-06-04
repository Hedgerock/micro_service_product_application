package com.hedgerock.catalogue.config;

import com.github.loki4j.client.http.HttpHeader;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.netflix.eureka.RestTemplateTimeoutProperties;
import org.springframework.cloud.netflix.eureka.http.DefaultEurekaClientHttpRequestFactorySupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.List;

@Configuration
public class DiscoveryBeans {

    @Bean
    public DefaultEurekaClientHttpRequestFactorySupplier DefaultEurekaClientHttpRequestFactorySupplier(
            RestTemplateTimeoutProperties restTemplateTimeoutProperties,
            ClientRegistrationRepository registrationRepository,
            OAuth2AuthorizedClientService auth2AuthorizedClientService
    ) {
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                registrationRepository, auth2AuthorizedClientService
        );

        return new DefaultEurekaClientHttpRequestFactorySupplier(restTemplateTimeoutProperties,
                List.of((request, entity, context) -> {
                    if (!request.containsHeader(HttpHeader.AUTHORIZATION)) {
                        var authorizedClient = manager.authorize(OAuth2AuthorizeRequest
                                .withClientRegistrationId("discovery")
                                .principal("catalogue-service-metrics-client")
                                .build()
                        );

                        request.setHeader(HttpHeaders.AUTHORIZATION,
                                "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                        ;
                    }
                })
        );
    }

}
