package com.hedgerock.feedback.service.product_review_option_service;

import com.hedgerock.feedback.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewService {
    Mono<ProductReview> addReview(Long productId, int rating, String review, String userId);

    Flux<ProductReview> findProductReviewsByProduct(Long productId);
}
