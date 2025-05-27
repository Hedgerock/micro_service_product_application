package com.hedgerock.customer.client;

import com.hedgerock.customer.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductsClient {

    Flux<FavouriteProduct> getFavouriteProducts();

    Mono<FavouriteProduct> findFavouriteProductByProductId(Long productId);
    Mono<FavouriteProduct> addProductToFavourite(Long productId);
    Mono<Void> removeProductFromFavourite(Long productId);
}
