package com.hedgerock.customer.controller.payload;

public record NewProductPayload(
        Integer rating,
        String review
) {
}
