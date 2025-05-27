package com.hedgerock.customer.client;

import com.hedgerock.customer.client.exception.BadRequestClientException;
import com.hedgerock.customer.client.payload.NewFavouriteProductPayload;
import com.hedgerock.customer.entity.FavouriteProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class WebFavouriteProductsClient implements FavouriteProductsClient {
    private final WebClient webClient;

    @Override
    public Flux<FavouriteProduct> getFavouriteProducts() {
        return this.webClient.get()
                .uri("/feedback-api/favourite-products")
                .retrieve()
                .bodyToFlux(FavouriteProduct.class);
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProductId(Long productId) {
        return this.webClient.get()
                .uri("/feedback-api/favourite-products/by-product-id/{productId}", productId)
                .retrieve()
                .bodyToMono(FavouriteProduct.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }

    @Override
    public Mono<FavouriteProduct> addProductToFavourite(Long productId) {
        return this.webClient.post()
                .uri("/feedback-api/favourite-products")
                .bodyValue(new NewFavouriteProductPayload(productId))
                .retrieve()
                .bodyToMono(FavouriteProduct.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        exception -> new BadRequestClientException(
                                exception,
                                ((List<String>) exception.getResponseBodyAs(ProblemDetail.class).getProperties().get("errors"))
                ));
    }

    @Override
    public Mono<Void> removeProductFromFavourite(Long productId) {
        return this.webClient.delete()
                .uri("/feedback-api/favourite-products/by-product-id/{productId}", productId)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }
}
