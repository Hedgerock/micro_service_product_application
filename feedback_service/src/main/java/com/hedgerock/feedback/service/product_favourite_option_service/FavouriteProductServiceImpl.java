package com.hedgerock.feedback.service.product_favourite_option_service;

import com.hedgerock.feedback.entity.FavouriteProduct;
import com.hedgerock.feedback.repository.FavouriteProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FavouriteProductServiceImpl implements FavoriteProductService {

    private final FavouriteProductRepository favouriteProductRepository;

    @Override
    public Mono<FavouriteProduct> addProductToFavourite(Long productId, String userId) {
        return this.favouriteProductRepository
                .save(new FavouriteProduct(UUID.randomUUID(), productId, userId));
    }

    @Override
    public Mono<Void> removeProductFromFavourite(Long productId, String userId) {
        return this.favouriteProductRepository.deleteByProductIdAndUserId(productId, userId);
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProductId(Long productId, String userId) {
        return this.favouriteProductRepository.findByProductIdAndUserId(productId, userId);
    }

    @Override
    public Flux<FavouriteProduct> getAllFavouriteProducts(String userId) {
        return this.favouriteProductRepository.findAllByUserId(userId);
    }
}
