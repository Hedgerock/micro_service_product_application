package com.hedgerock.feedback.service.product_review_option_service;

import com.hedgerock.feedback.entity.ProductReview;
import com.hedgerock.feedback.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;

    @Override
    public Mono<ProductReview> addReview(Long productId, int rating, String review, String userId) {
        return this.productReviewRepository.save(
                new ProductReview(UUID.randomUUID(), productId, rating, review, userId)
        );
    }

    @Override
    public Flux<ProductReview> findProductReviewsByProduct(Long productId) {
        return this.productReviewRepository.findAllByProductId(productId);
    }
}
