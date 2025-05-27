package com.hedgerock.customer.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.hedgerock.customer.utils.body.ProductsCredentials;
import com.hedgerock.customer.utils.body.ProductsFavouriteCredentials;
import com.hedgerock.customer.utils.body.ProductsReviewCredentials;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
@WireMockTest(httpPort = 54321)
class CurrentProductControllerIntegrationTests {
    private static final String DEFAULT_CATALOGUE_API_URL = "/catalogue-api/products/1";
    private static final String DEFAULT_CUSTOMER_REQUEST_PATH = "/customer/products/list/1";
    private static final String DEFAULT_FEEDBACK_API_URL = "/feedback-api/favourite-products";

    private static final String DEFAULT_REVIEW_API_POST_URL = "/feedback-api/product-reviews";
    private static final String DEFAULT_REVIEW_API_URL = DEFAULT_REVIEW_API_POST_URL + "/by-product-id/1";

    private static final String DEFAULT_FAVOURITE_API_POST_URL = "/feedback-api/favourite-products";

    private static final String CUSTOMIZED_CUSTOMER_REQUEST_PATH = "/customer/products/list/%d";
    private static final String CUSTOMIZED_CUSTOMER_REQUEST_PATH_CATALOGUE_API_URL = "/catalogue-api/products/%d";
    private static final String CUSTOMIZED_REVIEW_API_URL = DEFAULT_REVIEW_API_POST_URL + "/by-product-id/%d";

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        WireMock.stubFor(WireMock.get(DEFAULT_CATALOGUE_API_URL)
                .willReturn(WireMock
                        .okJson(ProductsCredentials.getProductsBodyJSONFormat())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                ));
    }

    @Test
    void createReview_Success_ReturnToTheCurrentProductPage() {
        final String NEW_REVIEW_BODY = ProductsReviewCredentials.getProductReview(1, true);

        WireMock.stubFor(WireMock.post(DEFAULT_REVIEW_API_POST_URL)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(WireMock.equalToJson(NEW_REVIEW_BODY))
                .willReturn(WireMock.created()
                        .withHeader(
                                HttpHeaders.LOCATION,
                                "http://localhost"
                                        + DEFAULT_REVIEW_API_POST_URL + "/"
                                        + (ProductsReviewCredentials.DEFAULT_REVIEW_UUID + 1))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(ProductsReviewCredentials.getProductReview(1))
                )
        );

        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri(DEFAULT_CUSTOMER_REQUEST_PATH + "/create-review")
                .body(BodyInserters.fromFormData("rating", "5").with("review", "Review number 1"))

                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location(DEFAULT_CUSTOMER_REQUEST_PATH);

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathMatching(DEFAULT_REVIEW_API_POST_URL))
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(WireMock.equalToJson(NEW_REVIEW_BODY))
        );

    }

    @Test
    void createReview_IsInvalidCredentials_ReturnToTheCurrentProductPage() throws Exception {
        final String BAD_CREDENTIALS = ProductsReviewCredentials.getInvalidCredentials();

        WireMock.stubFor(WireMock.post(DEFAULT_REVIEW_API_POST_URL)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(WireMock.equalToJson(BAD_CREDENTIALS))
                .willReturn(WireMock.badRequest()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody(ProductsReviewCredentials.getErrors())

                )
        );

        WireMock.stubFor(WireMock.get(DEFAULT_FAVOURITE_API_POST_URL + "/by-product-id/1")
                .willReturn(WireMock
                        .okJson(ProductsFavouriteCredentials.getFavouriteCredentials())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri(DEFAULT_CUSTOMER_REQUEST_PATH + "/create-review")
                .body(BodyInserters.fromFormData("rating", "-1").with("review", "Super giant text"))

                .exchange()
                .expectStatus().isBadRequest();

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathMatching(DEFAULT_REVIEW_API_POST_URL))
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(WireMock.equalToJson(BAD_CREDENTIALS))
        );

    }

    @Test
    void createReview_UserIsNotAuthorized_ReturnToTheLoginPage() {

        this.webTestClient
                .mutateWith(csrf())
                .post()
                .uri(DEFAULT_CUSTOMER_REQUEST_PATH + "/create-review")
                .body(BodyInserters.fromFormData("rating", "5").with("review", "Review number 1"))

                .exchange()
                .expectStatus().isFound()
                .expectHeader().location("/login");

    }

    @Test
    void getCurrentPage_Success_ReturnCurrentProductPage() {
        final String REVIEWS = ProductsReviewCredentials.getProductReview(3);

        WireMock.stubFor(WireMock.get(DEFAULT_REVIEW_API_URL)
                .willReturn(WireMock.okJson(REVIEWS)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );

        WireMock.stubFor(WireMock.get(DEFAULT_FEEDBACK_API_URL + "/by-product-id/1")
                .willReturn(WireMock.created()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(ProductsCredentials.getFavouriteProductId(true))
                )
        );

        this.webTestClient
                .mutateWith(mockUser())
                .get()
                .uri(DEFAULT_CUSTOMER_REQUEST_PATH)
                .exchange()

                .expectStatus().isOk();


        WireMock.verify(WireMock.getRequestedFor(WireMock
                .urlPathMatching(DEFAULT_CATALOGUE_API_URL)));

        WireMock.verify(WireMock.getRequestedFor(WireMock
                .urlPathMatching(DEFAULT_REVIEW_API_URL)));

        WireMock.verify(WireMock.getRequestedFor(WireMock
                .urlPathMatching(DEFAULT_FEEDBACK_API_URL + "/by-product-id/1")));
    }

    @Test
    void getCurrentPage_ProductNotFound_Return404Error() {
        final int IDENTIFICATION_VALUE = 3333;

        final String INVALID_PATH = String.format(CUSTOMIZED_CUSTOMER_REQUEST_PATH, IDENTIFICATION_VALUE);
        final String INVALID_REVIEWS_PATH = String.format(CUSTOMIZED_REVIEW_API_URL, IDENTIFICATION_VALUE);
        final String REVIEWS = ProductsReviewCredentials.getProductReview(3);

        WireMock.stubFor(WireMock.get(INVALID_REVIEWS_PATH)
                .willReturn(WireMock.okJson(REVIEWS)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );

        this.webTestClient
                .mutateWith(mockUser())
                .get()
                .uri(INVALID_PATH)
                .exchange()

                .expectStatus().isNotFound();

        WireMock.verify(WireMock.getRequestedFor(
                WireMock.urlPathMatching(String.format(CUSTOMIZED_CUSTOMER_REQUEST_PATH_CATALOGUE_API_URL, IDENTIFICATION_VALUE))
        ));
    }

    @Test
    void getCurrentPage_UserIsNotAuthorized_Return404Error() {

        this.webTestClient
                .get()
                .uri(DEFAULT_REVIEW_API_URL)
                .exchange()

                .expectStatus().isFound()
                .expectHeader().location("/login");
    }

    @Test
    void addToFavourites_RequestIsValid_ReturnToTheProductPage() {

        WireMock.stubFor(WireMock.post(DEFAULT_FEEDBACK_API_URL)
                .withRequestBody(WireMock.equalToJson(ProductsCredentials.getFavouriteProductId()))
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(WireMock.created()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(ProductsCredentials.getFavouriteProductId(true))
                )
        );

        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri(DEFAULT_CUSTOMER_REQUEST_PATH + "/add-to-favourites")

                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location(DEFAULT_CUSTOMER_REQUEST_PATH);


        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching(DEFAULT_CATALOGUE_API_URL)));
        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathMatching(DEFAULT_FEEDBACK_API_URL))
                .withRequestBody(WireMock.equalToJson(ProductsCredentials.getFavouriteProductId())));
    }

    @Test
    void addToFavourites_ProductNotExist_ReturnNotFoundPage() {
        final String INVALID_CUSTOMER_REQUEST_PATH = String.format(CUSTOMIZED_CUSTOMER_REQUEST_PATH, 2222);

        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri(INVALID_CUSTOMER_REQUEST_PATH + "/add-to-favourites")

                .exchange()
                .expectStatus().isNotFound();

        WireMock.verify(WireMock.getRequestedFor(WireMock
                .urlPathMatching(String.format(CUSTOMIZED_CUSTOMER_REQUEST_PATH_CATALOGUE_API_URL, 2222))));
    }

    @Test
    void addToFavourites_UserIsNotAuthorized_ReturnLoginPage() {

        this.webTestClient
                .mutateWith(csrf())
                .post()
                .uri(DEFAULT_CUSTOMER_REQUEST_PATH + "/add-to-favourites")

                .exchange()
                .expectStatus().isFound()
                .expectHeader().location("/login");

    }

    @Test
    void addToFavourites_InvalidCsrfToken_ReturnNotFoundPage() {

        this.webTestClient
                .mutateWith(mockUser())
                .post()
                .uri(DEFAULT_CUSTOMER_REQUEST_PATH + "/add-to-favourites")

                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void removeFromFavourites_Success_ReturnToTheProductPage() {

        WireMock.stubFor(WireMock.delete(DEFAULT_FEEDBACK_API_URL + "/by-product-id/1")
                .willReturn(WireMock.noContent())
        );

        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri(DEFAULT_CUSTOMER_REQUEST_PATH + "/remove-from-favourites")

                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location(DEFAULT_CUSTOMER_REQUEST_PATH);


        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching(DEFAULT_CATALOGUE_API_URL)));
        WireMock.verify(WireMock.deleteRequestedFor(WireMock
                .urlPathMatching(DEFAULT_FEEDBACK_API_URL + "/by-product-id/1")));
    }

    @Test
    void removeFromFavourites_ProductNotFound_ReturnToTheProductPage() {
        final String INVALID_DELETE_REQUEST_PATH = String.format(CUSTOMIZED_CUSTOMER_REQUEST_PATH, 1111);

        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri(INVALID_DELETE_REQUEST_PATH + "/remove-from-favourites")

                .exchange()
                .expectStatus().isNotFound();

        WireMock.verify(WireMock.getRequestedFor(WireMock
                .urlPathMatching(String.format(CUSTOMIZED_CUSTOMER_REQUEST_PATH_CATALOGUE_API_URL, 1111))));
    }

    @Test
    void removeFromFavourites_UserNotAuthorized_ReturnToTheLoginPage() {
        this.webTestClient
                .mutateWith(csrf())
                .post()
                .uri(DEFAULT_CUSTOMER_REQUEST_PATH + "/remove-from-favourites")

                .exchange()
                .expectStatus().isFound()
                .expectHeader().location("/login");
    }

    @Test
    void removeFromFavourites_InvalidCsrfToken_ReturnToTheProductPage() {

        this.webTestClient
                .mutateWith(csrf())
                .post()
                .uri(DEFAULT_CUSTOMER_REQUEST_PATH + "/remove-from-favourites")

                .exchange()
                .expectStatus().isFound();
    }

}