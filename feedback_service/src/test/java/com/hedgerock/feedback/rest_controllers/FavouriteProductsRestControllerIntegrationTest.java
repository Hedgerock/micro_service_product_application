package com.hedgerock.feedback.rest_controllers;

import com.hedgerock.feedback.entity.FavouriteProduct;
import com.hedgerock.utils.FavouriteProductsSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
public class FavouriteProductsRestControllerIntegrationTest extends FavouriteProductsSetup {
    private static final String BASE_FAVOURITE_API_URL = "/feedback-api/favourite-products";
    private static final String DOCUMENT_PATH = "feedback/product_review/%s";

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
    void initProductAsFavourite_UnauthorizedUser_Return401Status() {

        this.webTestClient
                .post()
                .uri(BASE_FAVOURITE_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(getSingleIdJSON(null))
                .exchange()
                .expectStatus().isUnauthorized();

    }

    @Test
    void initProductAsFavourite_BadRequest_ReturnBadRequestStatus() {

        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject(USER_ID)))
                .post()
                .uri(BASE_FAVOURITE_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(getSingleIdJSON(null))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody().json(getErrors());

    }

    @Test
    void initProductAsFavourite_Success_ReturnCreatedProductJSON() {

        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject(USER_ID)))
                .post()
                .uri(BASE_FAVOURITE_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(getSingleIdJSON(FOURTH_FAVOURITE_PRODUCT.getProductId()))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json(getSingleFavouriteProductJSON(FOURTH_FAVOURITE_PRODUCT, false)) //uuid, productId, userId
                    .jsonPath("$.id").exists()
                .consumeWith(document(DOCUMENT_PATH.formatted("add_product_to_favourite"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").description("Product identification value").type("Long")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Favourite product identification value").type("UUID"),
                                fieldWithPath("productId").description("Product identification value").type("Long"),
                                fieldWithPath("userId").description("User identification value").type("String")
                        )
                ));

    }

    @Test
    void removeProductFromFavourites_Success_ReturnNoContent() {
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject(USER_ID)))
                .delete()
                .uri(BASE_FAVOURITE_API_URL + "/by-product-id/1")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(document(DOCUMENT_PATH.formatted("remove_product_from_favourites")));
    }

    @Test
    void removeProductFromFavourites_IsNotAuthorized_Return401Status() {
        this.webTestClient
                .delete()
                .uri(BASE_FAVOURITE_API_URL + "/by-product-id/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void findFavouriteProductById_UserNotAuthorized_ReturnNotAuthorized() {
        this.webTestClient
                .get()
                .uri(BASE_FAVOURITE_API_URL + "/by-product-id/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void findFavouriteProductById_ProductNotFound_ReturnEmptyResult() {
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject(USER_ID)))
                .get()
                .uri(BASE_FAVOURITE_API_URL + "/by-product-id/1111")
                .exchange()
                .expectBody();
    }

    @Test
    void findFavouriteProductById_ProductFound_ReturnCurrentProduct() {
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject(USER_ID)))
                .get()
                .uri(BASE_FAVOURITE_API_URL + "/by-product-id/1")
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json(getSingleFavouriteProductJSON(FIRST_FAVOURITE_PRODUCT))
                .consumeWith(document(DOCUMENT_PATH.formatted("find_favourite_product_by_product_id"),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Favourite product identification value").type("UUID"),
                                fieldWithPath("productId").description("Product identification value").type("Long"),
                                fieldWithPath("userId").description("User identification value").type("String")
                        )
                ));
    }

    @Test
    void getFavouriteProduct_FavouriteProductFound_ReturnBodyOfFavouritesProducts() {
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject(USER_ID)))
                .get()
                .uri(BASE_FAVOURITE_API_URL)
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json(getResponseOfFavouriteProducts())
                .consumeWith(document(DOCUMENT_PATH.formatted("get_favourite_products"),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Favourite product identification value").type("UUID"),
                                fieldWithPath("[].productId").description("Product identification value").type("Long"),
                                fieldWithPath("[].userId").description("User identification value").type("String")
                        )
                ));
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
