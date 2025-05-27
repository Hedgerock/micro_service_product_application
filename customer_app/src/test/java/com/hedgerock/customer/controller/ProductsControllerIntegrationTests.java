package com.hedgerock.customer.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.hedgerock.customer.utils.body.ProductsFavouriteCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import wiremock.org.eclipse.jetty.http.HttpHeader;

import static com.hedgerock.customer.utils.body.GetProductsCredentials.getProducts;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@SpringBootTest
@AutoConfigureWebTestClient
@WireMockTest(httpPort = 54321)
class ProductsControllerIntegrationTests {
    private static final String QUERY_PARAM = "test";

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        String products = getProducts();

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("title", WireMock.equalTo(QUERY_PARAM))
                .willReturn(WireMock.okJson(products))
        );
    }

    @Test
    void getProductsListPage_Success_ReturnProductsList() {

        this.webTestClient
                .mutateWith(mockUser())
                .get()
                .uri("/customer/products/list?title="+ QUERY_PARAM)
                .exchange()

                .expectStatus().isOk();

        WireMock.verify(WireMock.getRequestedFor(
                WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("title", WireMock.equalTo(QUERY_PARAM)));
    }

    @Test
    void getProductsListPage_UnauthorizedUser_ReturnLoginPage() {

        this.webTestClient
                .get()
                .uri("/customer/products/list?title="+ QUERY_PARAM)
                .exchange()

                .expectStatus().isFound()
                .expectHeader().location("/login");
    }

    @Test
    void getFavouriteListPage_Success_ReturnFavouritesProductsList() {
        WireMock.stubFor(WireMock.get("/feedback-api/favourite-products")
                .willReturn(WireMock
                        .okJson(ProductsFavouriteCredentials.getFavouriteProducts())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );

        this.webTestClient
                .mutateWith(mockUser())
                .get()
                .uri("/customer/products/favourites?title="+ QUERY_PARAM)
                .exchange()

                .expectStatus().isOk();

        WireMock.verify(WireMock.getRequestedFor(
                        WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("title", WireMock.equalTo(QUERY_PARAM)));

        WireMock.verify(WireMock.getRequestedFor(
                        WireMock.urlPathMatching("/feedback-api/favourite-products")));
    }

    @Test
    void getFavouriteListPage_UserIsNotAuthorized_ReturnLoginPage() {

        this.webTestClient
                .get()
                .uri("/customer/products/favourites?title="+ QUERY_PARAM)
                .exchange()

                .expectStatus().isFound()
                .expectHeader().location("/login");
    }

}