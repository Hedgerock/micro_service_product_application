package com.hedgerock.feedback.rest_controllers;

import com.hedgerock.feedback.entity.ProductReview;
import com.hedgerock.feedback.rest_controllers.payload.NewProductReviewPayload;
import com.hedgerock.feedback.service.product_review_option_service.ProductReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ProductReviewsRestControllerTests {
    private static final long PRODUCT_ID = 1L;

    @Mock
    ProductReviewService productReviewService;

    @InjectMocks
    ProductReviewsRestController productReviewsRestController;

    @Test
    void createProductReview_Success_ReturnProductReviewBody() {
        final UUID ID = UUID.fromString("7d4c200d-4d8e-48db-ab8c-5704cb236413");
        final int RATING = 4;
        final String REVIEW_VALUE = "Something is not giving me to grade this product more than 4 out of 5";
        final String USER_ID = "a484988d-6cc8-427a-ab27-1620393cf1a6";

        var productReview = new ProductReview(ID, PRODUCT_ID, RATING, REVIEW_VALUE, USER_ID);
        //given
        Mockito.doReturn(Mono.just(productReview))
                .when(this.productReviewService).addReview(PRODUCT_ID, RATING, REVIEW_VALUE, USER_ID);
        //when
        StepVerifier
                .create(this.productReviewsRestController.createNewProductReview(
                    Mono.just(new JwtAuthenticationToken(Jwt
                            .withTokenValue("header.payload.signature")
                            .headers(header -> header.put("key", "value"))
                            .claim("sub", USER_ID)
                            .build())),
                    UriComponentsBuilder.fromUriString("http://localhost"),
                    Mono.just(new NewProductReviewPayload(PRODUCT_ID, RATING, REVIEW_VALUE))))

                .expectNext(ResponseEntity.created(URI.create("http://localhost/feedback-api/product-reviews/" + ID))
                        .body(new ProductReview(ID, PRODUCT_ID, RATING, REVIEW_VALUE, USER_ID)))

                .verifyComplete();

        Mockito.verify(this.productReviewService).addReview(PRODUCT_ID, RATING, REVIEW_VALUE, USER_ID);
        Mockito.verifyNoMoreInteractions(this.productReviewService);
    }

    @Test
    void getProductReviewsByProductId_ProductsFound_ReturnFluxOfProducts() {
        var firstProductReview = new ProductReview(
                UUID.fromString("8dcb3250-7899-4384-9d26-209ed91f108f"),
                PRODUCT_ID,
                5,
                "I like this product :)",
                "4324f8b1-90d9-4280-ace2-5b9bbcca2aae"
        );

        var secondProductReview = new ProductReview(
                UUID.fromString("35f1be31-947b-46a4-92d7-5b0f2a5f760f"),
                PRODUCT_ID,
                3,
                "Expected better one :(",
                "4b9926a6-bf77-4543-b362-171dcf8fecc5"
        );

        var productReviews = List.of(firstProductReview, secondProductReview);

        //given
        Mockito.doReturn(Flux.fromIterable(productReviews))
                .when(this.productReviewService).findProductReviewsByProduct(PRODUCT_ID);
        //when

        StepVerifier.create(this.productReviewsRestController.getProductReviewsByProductId(PRODUCT_ID))
        //then
                .expectNext(firstProductReview, secondProductReview)
                .verifyComplete();

        Mockito.verify(this.productReviewService).findProductReviewsByProduct(PRODUCT_ID);
        Mockito.verifyNoMoreInteractions(this.productReviewService);

    }

    @Test
    void getProductReviewsByProductId_ProductsNotFound_ReturnEmptyFlux() {

        //given
        Mockito.doReturn(Flux.empty())
                .when(this.productReviewService).findProductReviewsByProduct(PRODUCT_ID);
        //when

        StepVerifier.create(this.productReviewsRestController.getProductReviewsByProductId(PRODUCT_ID))
                //then
                .expectNext()
                .verifyComplete();

        Mockito.verify(this.productReviewService).findProductReviewsByProduct(PRODUCT_ID);
        Mockito.verifyNoMoreInteractions(this.productReviewService);

    }
}
