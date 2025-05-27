package com.hedgerock.manager.entities;

import com.hedgerock.manager.payload.UpdateProductPayload;

public record Product(long id, String title, String details) {

    public UpdateProductPayload getUpdatePayload() {
        return new UpdateProductPayload(title, details);
    }
}
