package com.hedgerock.feedback.rest_controllers;

import com.hedgerock.feedback.entity.FavouriteProduct;
import com.hedgerock.feedback.rest_controllers.payload.NewFavouriteProductPayload;
import com.hedgerock.feedback.service.product_favourite_option_service.FavoriteProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("feedback-api/favourite-products")
public class FavouriteProductsRestController {

    private final FavoriteProductService favoriteProductService;

    @GetMapping
    public Flux<FavouriteProduct> getFavouriteProduct(
            Mono<JwtAuthenticationToken> authenticationTokenMono
    ) {
        return authenticationTokenMono.flatMapMany(token ->
                this.favoriteProductService.getAllFavouriteProducts(token.getToken().getSubject()));
    }

    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavouriteProduct> findFavouriteProductById(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @PathVariable("productId") Long productId
    ) {
        return authenticationTokenMono.flatMap(token ->
            this.favoriteProductService.findFavouriteProductByProductId(productId, token.getToken().getSubject())
        );
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProduct>> initProductAsFavourite(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            UriComponentsBuilder componentsBuilder,
            @Valid @RequestBody Mono<NewFavouriteProductPayload> productPayload
            ) {
        return authenticationTokenMono.flatMap(token ->
                Mono.zip(authenticationTokenMono, productPayload)
                        .flatMap(tuple -> this.favoriteProductService
                                .addProductToFavourite(tuple.getT2().productId(), tuple.getT1().getToken().getSubject()))
                        .map(product -> ResponseEntity
                                .created(componentsBuilder
                                        .replacePath("/feedback-api/favourite-products/{productId}")
                                        .build(product.getId()))
                                .body(product)
                        )
                );
    }

    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavourites(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @PathVariable("productId") Long productId
    ) {
        return authenticationTokenMono.flatMap(token ->
                this.favoriteProductService.removeProductFromFavourite(productId, token.getToken().getSubject())
                    .then(Mono.just(ResponseEntity.noContent().build())));
    }
}
