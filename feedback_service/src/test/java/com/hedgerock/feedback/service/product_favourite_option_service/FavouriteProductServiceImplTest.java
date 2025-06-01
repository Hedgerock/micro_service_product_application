package com.hedgerock.feedback.service.product_favourite_option_service;

import com.hedgerock.feedback.repository.FavouriteProductRepository;
import com.hedgerock.utils.FavouriteProductsSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FavouriteProductServiceImplTest extends FavouriteProductsSetup {
    private static final Long PRODUCT_ID = FIRST_FAVOURITE_PRODUCT.getProductId();

    @Mock
    FavouriteProductRepository favouriteProductRepository;

    @InjectMocks
    FavouriteProductServiceImpl favouriteProductService;

    @Test
    void getAllFavouriteProducts() {

        //given
        Mockito.doReturn(Flux.fromIterable(FAVOUIRITE_PRODUCTS_LIST))
                .when(this.favouriteProductRepository).findAllByUserId(USER_ID);
        //when
        StepVerifier.create(this.favouriteProductService.getAllFavouriteProducts(USER_ID))
                .expectNext(FIRST_FAVOURITE_PRODUCT, SECOND_FAVOURITE_PRODUCT, THIRD_FAVOURITE_PRODUCT)
                .verifyComplete();
        //then

        Mockito.verify(this.favouriteProductRepository).findAllByUserId(USER_ID);
    }

    @Test
    void findFavouriteProductByProductId() {

        //given
        Mockito.doReturn(Mono.just(FIRST_FAVOURITE_PRODUCT))
                .when(this.favouriteProductRepository).findByProductIdAndUserId(PRODUCT_ID, USER_ID);
        //when
        StepVerifier.create(this.favouriteProductService.findFavouriteProductByProductId(PRODUCT_ID, USER_ID))
        //then
                .expectNext(FIRST_FAVOURITE_PRODUCT)
                .verifyComplete();

        Mockito.verify(this.favouriteProductRepository).findByProductIdAndUserId(PRODUCT_ID, USER_ID);
    }

    @Test
    void removeProductFromFavourite() {

        //given
        Mockito.doReturn(Mono.empty())
                .when(this.favouriteProductRepository).deleteByProductIdAndUserId(PRODUCT_ID, USER_ID);
        //when
        StepVerifier.create(this.favouriteProductService.removeProductFromFavourite(PRODUCT_ID, USER_ID))
                .verifyComplete();
        //then

        Mockito.verify(this.favouriteProductRepository).deleteByProductIdAndUserId(PRODUCT_ID, USER_ID);
    }

    @Test
    void addProductToFavourite() {

        //given
        Mockito.doAnswer(invocation -> Mono.justOrEmpty(invocation.getArguments()[0]))
                .when(this.favouriteProductRepository).save(Mockito.any());
        //when
        StepVerifier.create(this.favouriteProductService.addProductToFavourite(PRODUCT_ID, USER_ID))
        //then
                .expectNextMatches(favouriteProduct -> validateFavouriteProductsFields(favouriteProduct, PRODUCT_ID))
                .verifyComplete();

        Mockito.verify(this.favouriteProductRepository)
                .save(Mockito.argThat(favouriteProduct -> validateFavouriteProductsFields(favouriteProduct, PRODUCT_ID)));
    }
}