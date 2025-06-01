package com.hedgerock.catalogue.payload;

import com.hedgerock.catalogue.interfaces.ProductPayload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProductPayload(
        @NotNull(message = "{catalogue.product.update.empty_value_message}")
        @NotBlank(message = "{catalogue.product.update.empty_value_message}")
        @Size(min = 3, max = 50, message = "{catalogue.product.update.wrong_size_message}")
        String title,

        @Size(max=1000, message = "{catalogue.product.update.wrong_details_size_message}")
        String details
) implements ProductPayload {
        @Override
        public String getTitle() {
                return title;
        }

        @Override
        public String getDetails() {
                return details;
        }
}
