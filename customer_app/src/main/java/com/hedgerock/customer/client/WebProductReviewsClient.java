package com.hedgerock.customer.client;

import com.hedgerock.customer.client.exception.BadRequestClientException;
import com.hedgerock.customer.client.payload.NewProductReviewPayload;
import com.hedgerock.customer.entity.ProductReview;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class WebProductReviewsClient implements ProductReviewsClient {

    private final WebClient webClient;

    @Override
    public Flux<ProductReview> getProductReviewsByProductId(Long productId) {
        return this.webClient.get()
                .uri("/feedback-api/product-reviews/by-product-id/{productId}", productId)
                .retrieve()
                .bodyToFlux(ProductReview.class);
    }

    @Override
    public Mono<ProductReview> addReview(Long productId, Integer rating, String review) {
        return this.webClient.post()
                .uri("/feedback-api/product-reviews")
                .bodyValue(new NewProductReviewPayload(productId, rating, review))
                .retrieve()
                .bodyToMono(ProductReview.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        exception -> new BadRequestClientException(
                                exception,
                                ((List<String>) exception.getResponseBodyAs(ProblemDetail.class).getProperties().get("errors"))
                        ));
    }
}
