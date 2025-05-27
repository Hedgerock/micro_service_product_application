package com.hedgerock.customer.client;

import com.hedgerock.customer.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewsClient {

    Flux<ProductReview> getProductReviewsByProductId(Long productId);
    Mono<ProductReview> addReview(Long productId, Integer rating, String review);

}
