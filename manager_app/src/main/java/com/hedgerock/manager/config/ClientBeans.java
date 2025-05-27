package com.hedgerock.manager.config;

import com.hedgerock.manager.client.ProductRestClientImpl;
import com.hedgerock.manager.security.OauthClientHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

    @Bean
    ProductRestClientImpl restClient(
            @Value("${product.services.catalogue.uri:http://localhost:8081}") String catalogueBaseURI,
            @Value("${product.services.catalogue.username:}") String username,
            @Value("${product.services.catalogue.password:}") String password,

            @Value("${product.services.catalogue.registration-id:manager-app}") String registrationId,
            ClientRegistrationRepository registrationRepository,
            OAuth2AuthorizedClientRepository clientRepository
    ) {
        return new ProductRestClientImpl(
                RestClient.builder()
                        .baseUrl(catalogueBaseURI)
                        .requestInterceptor(new OauthClientHttpRequestInterceptor(
                                new DefaultOAuth2AuthorizedClientManager(registrationRepository, clientRepository),
                                registrationId
                        ))
//                        .requestInterceptor(new BasicAuthenticationInterceptor(username, password))
                        .build()
        );
    }

}
