package com.hedgerock.feedback.rest_controllers.payload;

import jakarta.validation.constraints.NotNull;

public record NewFavouriteProductPayload(
        @NotNull(message = "{feedback.products.favourites.product_id.empty_product_id_message}")
        Long productId
) {
}
