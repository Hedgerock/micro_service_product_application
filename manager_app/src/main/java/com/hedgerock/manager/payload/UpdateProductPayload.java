package com.hedgerock.manager.payload;

import com.hedgerock.manager.interfaces.ProductPayload;

public record UpdateProductPayload(String title, String details) implements ProductPayload {
        @Override
        public String getTitle() {
                return title;
        }

        @Override
        public String getDetails() {
                return details;
        }
}
