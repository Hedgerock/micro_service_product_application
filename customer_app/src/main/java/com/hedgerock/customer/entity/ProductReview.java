package com.hedgerock.customer.entity;

import java.util.UUID;

public record ProductReview(UUID id, long productId, int rating, String review) {
}
