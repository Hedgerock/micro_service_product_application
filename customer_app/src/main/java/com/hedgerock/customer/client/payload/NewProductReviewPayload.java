package com.hedgerock.customer.client.payload;

public record NewProductReviewPayload(Long productId, Integer rating, String review) {
}
