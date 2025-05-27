package com.hedgerock.customer.config;

import com.hedgerock.customer.client.WebFavouriteProductsClient;
import com.hedgerock.customer.client.WebProductReviewsClient;
import com.hedgerock.customer.client.WebProductsClient;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TestBeans {

    @Bean
    public ReactiveClientRegistrationRepository reactiveClientRegistrationRepository() {
        return Mockito.mock();
    }
    @Bean
    public ServerOAuth2AuthorizedClientRepository serverOAuth2AuthorizedClientRepository() {
        return Mockito.mock();
    }

    @Bean
    @Primary
    public WebProductsClient mockWebProductsClient(
            @Value("${test.host.url}") String baseUrl
    ) {
        return new WebProductsClient(WebClient.builder()
                .baseUrl(baseUrl)
                .build()
        );
    }

    @Bean
    @Primary
    public WebFavouriteProductsClient mockWebFavouriteProductsClient(
            @Value("${test.host.url}") String baseUrl
    ) {
        return new WebFavouriteProductsClient(WebClient.builder().baseUrl(baseUrl).build());
    }

    @Bean
    @Primary
    public WebProductReviewsClient mockWebProductReviewsClient (
            @Value("${test.host.url}") String baseUrl
    ) {
        return new WebProductReviewsClient(WebClient.builder().baseUrl(baseUrl).build());
    }

}
