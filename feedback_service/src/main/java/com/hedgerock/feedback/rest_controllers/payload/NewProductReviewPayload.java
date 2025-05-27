package com.hedgerock.feedback.rest_controllers.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewProductReviewPayload(
        @NotNull(message = "{feedback.products.reviews.product_id.empty_product_id_message}")
        Long productId,

        @NotNull(message = "{feedback.products.reviews.rating.empty_rating_error_message}")
        @Min(value = 1, message = "{feedback.products.reviews.rating.out_of_bound_min_value_rating_message}")
        @Max(value = 5, message = "{feedback.products.reviews.rating.out_of_bound_max_value_rating_message}")
        Integer rating,

        @Size(max=1000, message = "{feedback.products.reviews.review.out_of_bound_review_message}")
        String review
) {
}
