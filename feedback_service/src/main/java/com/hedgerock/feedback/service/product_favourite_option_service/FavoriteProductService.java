package com.hedgerock.feedback.service.product_favourite_option_service;

import com.hedgerock.feedback.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavoriteProductService {
    Mono<FavouriteProduct> addProductToFavourite(Long productId, String userId);
    Mono<Void> removeProductFromFavourite(Long productId, String userId);
    Mono<FavouriteProduct>findFavouriteProductByProductId(Long productId, String userId);
    Flux<FavouriteProduct>getAllFavouriteProducts(String userId);
}
