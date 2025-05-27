package com.hedgerock.customer.client;

import com.hedgerock.customer.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductsClient {
    Flux<Product> findAllProducts(String title);
    Mono<Product> findProductById(Long productId);
}
