package com.hedgerock.feedback.rest_controllers;

import com.hedgerock.feedback.entity.FavouriteProduct;
import com.hedgerock.utils.FavouriteProductsSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest
@AutoConfigureWebTestClient
public class FavouriteProductsRestControllerIntegrationTest extends FavouriteProductsSetup {
    private static final String BASE_FAVOURITE_API_URL = "/feedback-api/favourite-products";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        this.reactiveMongoTemplate.insertAll(FAVOUIRITE_PRODUCTS_LIST).blockLast();
    }

    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(FavouriteProduct.class).all().block();
    }

    @Test
    void getFavouriteProduct_FavouriteProductFound_ReturnBodyOfFavouritesProducts() {
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject(USER_ID)))
                .get()
                .uri(BASE_FAVOURITE_API_URL)
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json(getResponseOfFavouriteProducts());
    }

    @Test
    void getFavouriteProduct_FavouriteProductNotFound_ReturnEmptyContent() {
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("Some user id which is not exists")))
                .get()
                .uri(BASE_FAVOURITE_API_URL)
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json("[]");
    }

    @Test
    void getFavouriteProduct_UserIsNotAuthorized_ReturnEmptyContent() {
        this.webTestClient
                .get()
                .uri(BASE_FAVOURITE_API_URL)
                .exchange()
                .expectStatus().isUnauthorized();
    }

}
