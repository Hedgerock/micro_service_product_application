package com.hedgerock.feedback.repository;

import com.hedgerock.feedback.entity.FavouriteProduct;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FavouriteProductRepository extends ReactiveCrudRepository<FavouriteProduct, UUID> {
    Mono<Void> deleteByProductIdAndUserId(Long productId, String userId);
    Mono<FavouriteProduct> findByProductIdAndUserId(Long productId, String userId);
    Flux<FavouriteProduct> findAllByUserId(String userId);
}
