package com.hedgerock.feedback.service.product_review_option_service;

import com.hedgerock.feedback.repository.ProductReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static com.hedgerock.utils.bodies.ReviewCredentials.*;

@ExtendWith(MockitoExtension.class)
class ProductReviewServiceImplTest {

    @Mock
    ProductReviewRepository productReviewRepository;

    @InjectMocks
    ProductReviewServiceImpl productReviewService;

    @Test
    void findProductReviewsByProduct() {

        //given
        Mockito.doReturn(Flux.fromIterable(getReviews()))
                .when(this.productReviewRepository).findAllByProductId(PRODUCT_ID);
        //when
        StepVerifier.create(this.productReviewService.findProductReviewsByProduct(PRODUCT_ID))
        //then
                .expectNext(FIRST_PRODUCT_REVIEW, SECOND_PRODUCT_REVIEW, THIRD_PRODUCT_REVIEW)
                .verifyComplete();

        Mockito.verify(this.productReviewRepository).findAllByProductId(PRODUCT_ID);
    }

    @Test
    void addReview() {
        //given
        Mockito.doAnswer(invocation -> Mono.justOrEmpty(invocation.getArguments()[0]))
                .when(this.productReviewRepository).save(Mockito.any());
        //when
        StepVerifier.create(this.productReviewService.addReview(
                PRODUCT_ID,
                RATING_VALUE,
                REVIEW_VALUE,
                USER_ID
        ))
        //then
                .expectNextMatches(productReview ->
                            productReview.getId() != null
                                && Objects.equals(productReview.getProductId(), PRODUCT_ID)
                                && productReview.getRating() == RATING_VALUE
                                && productReview.getReview().equals(REVIEW_VALUE)
                                && productReview.getUserId().equals(USER_ID)
                ).verifyComplete();

        Mockito.verify(this.productReviewRepository).save(Mockito.argThat(productReview ->
                productReview.getId() != null
                        && Objects.equals(productReview.getProductId(), PRODUCT_ID)
                        && productReview.getRating() == RATING_VALUE
                        && productReview.getReview().equals(REVIEW_VALUE)
                        && productReview.getUserId().equals(USER_ID)
        ));
    }

}