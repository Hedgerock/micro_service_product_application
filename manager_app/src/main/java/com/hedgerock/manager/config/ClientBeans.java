package com.hedgerock.manager.config;

import com.hedgerock.manager.client.ProductRestClientImpl;
import com.hedgerock.manager.security.OauthClientHttpRequestInterceptor;
import de.codecentric.boot.admin.client.registration.BlockingRegistrationClient;
import de.codecentric.boot.admin.client.registration.RegistrationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

    @Bean
    @ConditionalOnProperty(name = "spring.boot.admin.client.enabled", havingValue = "true")
    public RegistrationClient registrationClient(
            ClientRegistrationRepository registrationRepository,
            OAuth2AuthorizedClientService clientService
    ) {
        var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrationRepository, clientService);
        var restTemplate = new RestTemplateBuilder()
                .interceptors((request, body, execution) -> {
                    if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                        var authorizedClient = authorizedClientManager.authorize(OAuth2AuthorizeRequest
                                .withClientRegistrationId("manager-keycloak")
                                .principal("manager-application-metrics-client")
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
