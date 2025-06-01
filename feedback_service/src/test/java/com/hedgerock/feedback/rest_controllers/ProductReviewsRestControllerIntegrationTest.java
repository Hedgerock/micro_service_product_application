package com.hedgerock.feedback.rest_controllers;

import com.hedgerock.feedback.entity.ProductReview;
import com.hedgerock.utils.bodies.ReviewCredentials;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Slf4j
class ProductReviewsRestControllerIntegrationTest {
    private static final String DEFAULT_API_PATH = "/feedback-api/product-reviews";
    private static final String DEFAULT_USER_ID = "user-tester";
    private static final String DOCUMENT_PATH = "feedback/product_review/%s";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        this.reactiveMongoTemplate.insertAll(ReviewCredentials.getReviews()).blockLast();
    }


    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(ProductReview.class).all().block();
    }

    @Test
    void getProductReviewsByProductId_ReturnReviews() {

        this.webTestClient.mutateWith(mockJwt())
                .mutate().filter(ExchangeFilterFunction.ofRequestProcessor(ReviewCredentials::initClientRequest))
                .build()
                .get()
                .uri(DEFAULT_API_PATH + "/by-product-id/1")
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json(ReviewCredentials.getSearchReviewBody())
                .consumeWith(document(DOCUMENT_PATH.formatted("find_reviews_by_id"),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Review identification value").type("UUID"),
                                fieldWithPath("[].productId").description("Product identification value").type("Long"),
                                fieldWithPath("[].rating").description("Product rating value").type("Integer"),
                                fieldWithPath("[].review").description("User meaning about the product").type("String"),
                                fieldWithPath("[].userId").description("User identification value").type("String")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Expected content type APPLICATION_JSON")
                        )
                ));

    }

    @Test
    void getProductReviewsByProductId_UserIsNotAuthorized_ReturnNotAuthorized() {

        this.webTestClient
                .get()
                .uri(DEFAULT_API_PATH + "/by-product-id/1")
                .exchange()
                .expectStatus().isUnauthorized();

    }

    @Test
    void createNewProductReview_requestIsValidReturnCreatedProductReview() {
        //given

        //when
        this.webTestClient.mutateWith(mockJwt().jwt(builder -> builder.subject(DEFAULT_USER_ID)) )
                .post()
                .uri(DEFAULT_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ReviewCredentials.getCreateReviewBody())
        //then
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json(ReviewCredentials.getCreateReviewBody(DEFAULT_USER_ID)).jsonPath("$.id").exists()
                .consumeWith(document(DOCUMENT_PATH.formatted("create_product_review"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").description("Id of current product").type("Long as wrapper"),
                                fieldWithPath("rating").description("Rating from users of current product").type("Integer"),
                                fieldWithPath("review").description("User meaning about the product").type("String")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Id of current review").type("UUID"),
                                fieldWithPath("productId").description("Id of current product").type("Long as wrapper"),
                                fieldWithPath("rating").description("Rating from users of current product").type("Integer"),
                                fieldWithPath("review").description("User meaning about the product").type("String"),
                                fieldWithPath("userId").description("User identification value").type("String")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Reference to the current product")
                        )));
    }

    @Test
    void createNewProductReview_requestIsInvalidReturnBadRequest() {
        //given

        //when
        this.webTestClient.mutateWith(mockJwt().jwt(builder -> builder.subject(DEFAULT_USER_ID)) )
                .post()
                .uri(DEFAULT_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ReviewCredentials.getCreateReviewBody(true))
                //then
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody().json(ReviewCredentials.getErrors());
    }
}