package com.hedgerock.manager.config;

import com.hedgerock.manager.client.ProductRestClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

import static org.mockito.Mockito.mock;

@Configuration
public class TestBeans {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return mock();
    }

    @Bean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository() {
        return mock();
    }

    @Bean
    @Primary
    public ProductRestClientImpl productRestClient(
            @Value("${product.services.catalogue.uri:http://localhost:54321}") String catalogueBaseURI
    ) {
        return new ProductRestClientImpl(RestClient.builder()
                .baseUrl(catalogueBaseURI)
                .requestFactory(new JdkClientHttpRequestFactory())
                .build());
    }

}
