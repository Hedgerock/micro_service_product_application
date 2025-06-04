package com.hedgerock.customer.config;

import com.hedgerock.customer.client.WebFavouriteProductsClient;
import com.hedgerock.customer.client.WebProductReviewsClient;
import com.hedgerock.customer.client.WebProductsClient;
import de.codecentric.boot.admin.client.config.ClientProperties;
import de.codecentric.boot.admin.client.registration.ReactiveRegistrationClient;
import de.codecentric.boot.admin.client.registration.RegistrationClient;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {
    @Bean
    @Scope("prototype")
    public WebClient.Builder applicationServicesClientBuilder(
            ReactiveClientRegistrationRepository reactiveClientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository serverOAuth2AuthorizedClientRepository,
            ObservationRegistry observationRegistry
    ) {
        var filter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                reactiveClientRegistrationRepository,
                serverOAuth2AuthorizedClientRepository);

        filter.setDefaultClientRegistrationId("keycloak");

        return WebClient.builder()
                .observationRegistry(observationRegistry)
                .observationConvention(new DefaultClientRequestObservationConvention())
                .filter(filter);
    }

    @Bean
    public WebProductsClient webProductsClient(
            @Value("${product.services.catalogue.uri:http://localhost:8081}") String baseUrl,
            WebClient.Builder builder
    ) {
        return new WebProductsClient(builder
                .baseUrl(baseUrl)
                .build()
        );
    }

    @Bean
    WebFavouriteProductsClient webFavouriteProductsClient(
            @Value("${product.services.feedback.uri:http://localhost:8085}") String baseUrl,
            WebClient.Builder builder
    ) {
        return new WebFavouriteProductsClient(builder.baseUrl(baseUrl).build());
    }

    @Bean
    WebProductReviewsClient webProductReviewsClient (
            @Value("${product.services.feedback.uri:http://localhost:8085}") String baseUrl,
            WebClient.Builder builder
    ) {
        return new WebProductReviewsClient(builder.baseUrl(baseUrl).build());
    }

    @Bean
    @ConditionalOnProperty(value = "spring.boot.admin.client.enabled", havingValue = "true")
    public RegistrationClient registrationClient(
            ClientProperties clientProperties,
            ReactiveClientRegistrationRepository registrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService
    ) {
        var currentFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(registrationRepository, authorizedClientService)
        );

        currentFilter.setDefaultClientRegistrationId("admin-keycloak");

        return new ReactiveRegistrationClient(
                WebClient.builder()
                        .filter(currentFilter)
                        .build(),
                clientProperties.getReadTimeout()
        );
    }

}
