package com.hedgerock.feedback.rest_controllers;

import com.hedgerock.feedback.entity.FavouriteProduct;
import com.hedgerock.feedback.rest_controllers.payload.NewFavouriteProductPayload;
import com.hedgerock.feedback.service.product_favourite_option_service.FavoriteProductService;
import com.hedgerock.utils.FavouriteProductsSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class FavouriteProductsRestControllerTests extends FavouriteProductsSetup {

    @Mock
    FavoriteProductService favoriteProductService;

    @InjectMocks
    FavouriteProductsRestController favouriteProductsRestController;

    @Test
    void initProductAsFavourite() {
        final var ID = FIRST_FAVOURITE_PRODUCT.getId();
        final var PRODUCT_ID = FIRST_FAVOURITE_PRODUCT.getProductId();
        //given
        Mockito.doReturn(Mono.just(FIRST_FAVOURITE_PRODUCT))
                .when(this.favoriteProductService).addProductToFavourite(PRODUCT_ID, USER_ID);
        //when
        StepVerifier
                .create(this.favouriteProductsRestController.initProductAsFavourite(
                    getAuthenticationToken(),
                    UriComponentsBuilder.fromUriString("http://localhost"),
                    Mono.just(new NewFavouriteProductPayload(PRODUCT_ID))))
        //then
                .expectNext(ResponseEntity.created(URI.create("http://localhost/feedback-api/favourite-products/" + ID))
                        .body(new FavouriteProduct(ID, PRODUCT_ID, USER_ID)))
                .verifyComplete();

        Mockito.verify(this.favoriteProductService).addProductToFavourite(PRODUCT_ID, USER_ID);
        Mockito.verifyNoMoreInteractions(this.favoriteProductService);
    }

    @Test
    void removeProductFromFavourites() {
        final var PRODUCT_ID = 1L;

        //given
        Mockito.doReturn(Mono.empty())
                .when(this.favoriteProductService).removeProductFromFavourite(PRODUCT_ID, USER_ID);
        //when
        StepVerifier
                .create(this.favouriteProductsRestController
                        .removeProductFromFavourites(getAuthenticationToken(),PRODUCT_ID))
        //then
                .expectNext(ResponseEntity.noContent().build())
                .verifyComplete();

        Mockito.verify(this.favoriteProductService).removeProductFromFavourite(PRODUCT_ID, USER_ID);
        Mockito.verifyNoMoreInteractions(this.favoriteProductService);
    }

    @Test
    void findFavouriteProductById_ProductFound_ReturnMonoOfFoundFavouriteProduct() {
        final var PRODUCT_ID = 2L;
        //given
        Mockito.doReturn(Mono.just(SECOND_FAVOURITE_PRODUCT))
                .when(this.favoriteProductService).findFavouriteProductByProductId(PRODUCT_ID, USER_ID);
        //when
        StepVerifier
                .create(this.favouriteProductsRestController
                        .findFavouriteProductById(getAuthenticationToken(), PRODUCT_ID))
        //then
                .expectNext(SECOND_FAVOURITE_PRODUCT)
                .verifyComplete();

        Mockito.verify(this.favoriteProductService).findFavouriteProductByProductId(PRODUCT_ID, USER_ID);
        Mockito.verifyNoMoreInteractions(this.favoriteProductService);
    }

    @Test
    void findFavouriteProductById_ProductNotFound_ReturnEmptyMono() {
        final var PRODUCT_ID = 2L;
        //given
        Mockito.doReturn(Mono.empty())
                .when(this.favoriteProductService).findFavouriteProductByProductId(PRODUCT_ID, USER_ID);
        //when
        StepVerifier
                .create(this.favouriteProductsRestController
                        .findFavouriteProductById(getAuthenticationToken(), PRODUCT_ID))
                //then
                .expectNext()
                .verifyComplete();

        Mockito.verify(this.favoriteProductService).findFavouriteProductByProductId(PRODUCT_ID, USER_ID);
        Mockito.verifyNoMoreInteractions(this.favoriteProductService);
    }

    @Test
    void getFavouriteProduct_ProductsFound_ReturnFluxOfFavouriteProducts() {

        //given
        final var FAVOURITE_PRODUCTS_LIST = List.of(
                FIRST_FAVOURITE_PRODUCT,
                SECOND_FAVOURITE_PRODUCT,
                THIRD_FAVOURITE_PRODUCT
        );

        Mockito.doReturn(Flux.fromIterable(FAVOURITE_PRODUCTS_LIST))
                .when(this.favoriteProductService).getAllFavouriteProducts(USER_ID);
        //when
        StepVerifier
                .create(this.favouriteProductsRestController
                        .getFavouriteProduct(getAuthenticationToken()))
        //then
                .expectNext(FIRST_FAVOURITE_PRODUCT, SECOND_FAVOURITE_PRODUCT, THIRD_FAVOURITE_PRODUCT)
                .verifyComplete();

        Mockito.verify(this.favoriteProductService).getAllFavouriteProducts(USER_ID);
        Mockito.verifyNoMoreInteractions(this.favoriteProductService);
    }

    @Test
    void getFavouriteProduct_ProductsNotFound_ReturnFluxOfFavouriteProducts() {

        //given

        Mockito.doReturn(Flux.empty())
                .when(this.favoriteProductService).getAllFavouriteProducts(USER_ID);
        //when
        StepVerifier
                .create(this.favouriteProductsRestController
                        .getFavouriteProduct(getAuthenticationToken()))
        //then
                .expectNext()
                .verifyComplete();

        Mockito.verify(this.favoriteProductService).getAllFavouriteProducts(USER_ID);
        Mockito.verifyNoMoreInteractions(this.favoriteProductService);
    }

}
