package com.hedgerock.customer.controller;

import com.hedgerock.customer.client.FavouriteProductsClient;
import com.hedgerock.customer.client.ProductReviewsClient;
import com.hedgerock.customer.client.ProductsClient;
import com.hedgerock.customer.client.exception.BadRequestClientException;
import com.hedgerock.customer.controller.payload.NewProductPayload;
import com.hedgerock.customer.entity.FavouriteProduct;
import com.hedgerock.customer.entity.Product;
import com.hedgerock.customer.entity.ProductReview;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.hedgerock.customer.utils.body.ProductsCredentials.getReviews;
import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@ExtendWith(MockitoExtension.class)
class CurrentProductControllerTest {
    private static final long PRODUCT_ID = 1L;

    @Mock
    ProductsClient productsClient;

    @Mock
    FavouriteProductsClient favouriteProductsClient;

    @Mock
    ProductReviewsClient productReviewsClient;

    @InjectMocks
    CurrentProductController currentProductController;

    @Test
    void removeFromFavourites_Success_RemoveProductToFavouriteCollectionAndRedirectToTheCurrentProductPage() {
        final Product product = new Product(PRODUCT_ID, "Product title", "Product details");
        final FavouriteProduct favouriteProduct =
                new FavouriteProduct(UUID.fromString("8f44ef53-6c5c-4cca-bdf9-549a94a18efd"), PRODUCT_ID);

        //given
        Mockito.doReturn(Mono.just(favouriteProduct))
                .when(this.favouriteProductsClient).removeProductFromFavourite(PRODUCT_ID);

        //when
        var result = this.currentProductController.deleteFromFavourites(Mono.just(product));
        //then

        assertEquals(String.format("redirect:/customer/products/list/%d", PRODUCT_ID), result.block());
    }

    @Test
    void removeFromFavourites_ProductNotFound_RedirectToTheSamePage() {
        final Product product = new Product(PRODUCT_ID, "Product title", "Product details");

        //given
        Mockito.doReturn(Mono.error(new BadRequestClientException(new Throwable(), List.of("Product not found"))))
                .when(this.favouriteProductsClient).removeProductFromFavourite(PRODUCT_ID);

        //when
        var result = this.currentProductController.deleteFromFavourites(Mono.just(product));
        //then

        assertEquals(String.format("redirect:/customer/products/list/%d", PRODUCT_ID), result.block());
    }

    @Test
    void addToFavourites_Success_AddProductToFavouriteCollectionAndRedirectToTheCurrentProductPage() {
        final Product product = new Product(PRODUCT_ID, "Product title", "Product details");
        final FavouriteProduct favouriteProduct =
                new FavouriteProduct(UUID.fromString("8f44ef53-6c5c-4cca-bdf9-549a94a18efd"), PRODUCT_ID);

        //given
        Mockito.doReturn(Mono.just(favouriteProduct))
                .when(this.favouriteProductsClient).addProductToFavourite(PRODUCT_ID);

        //when
        var result = this.currentProductController.addToFavourites(Mono.just(product));
        //then

        assertEquals(String.format("redirect:/customer/products/list/%d", PRODUCT_ID), result.block());
    }

    @Test
    void addToFavourites_ProductNotFound_RedirectToTheSamePage() {
        final Product product = new Product(PRODUCT_ID, "Product title", "Product details");

        //given
        Mockito.doReturn(Mono.error(new BadRequestClientException(new Throwable(), List.of("Product not found"))))
                .when(this.favouriteProductsClient).addProductToFavourite(PRODUCT_ID);

        //when
        var result = this.currentProductController.addToFavourites(Mono.just(product));
        //then

        assertEquals(String.format("redirect:/customer/products/list/%d", PRODUCT_ID), result.block());
    }


    @Test
    void createReview_Success_CreateReviewAndRedirectToTheCurrentPage() {
        final int RATING_VALUE = 5;
        final String REVIEW_VALUE = "Test review value";
        final NewProductPayload productPayload = new NewProductPayload(RATING_VALUE, REVIEW_VALUE);
        final ProductReview productReview = new ProductReview(
                UUID.fromString("9782ee20-b22e-425d-a7cc-3af7351776ed"),
                PRODUCT_ID,
                RATING_VALUE,
                REVIEW_VALUE
        );
        //given
         var model = new ConcurrentModel();
         var response = new MockServerHttpResponse();

         Mockito.doReturn(Mono.just(productReview))
                 .when(this.productReviewsClient).addReview(PRODUCT_ID, RATING_VALUE, REVIEW_VALUE);
        //when
        var result = this.currentProductController.createReview(PRODUCT_ID, productPayload, model, response);
        //then

        assertEquals(String.format("redirect:/customer/products/list/%d", PRODUCT_ID), result.block());
    }

    @Test
    void createReview_BadRequest_ReturnTheSameViewWithExplanation() {
        final int RATING_VALUE = -1;
        final String REVIEW_VALUE = "Super long message (more than 1000 symbols)";
        final NewProductPayload badProductPayload = new NewProductPayload(RATING_VALUE, REVIEW_VALUE);
        final List<String> errors = List.of("Error 1", "Error 2");

        //given
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();

        Mockito.doReturn(Mono
                        .error(new BadRequestClientException(new Throwable(), errors))
                )
                .when(this.productReviewsClient).addReview(PRODUCT_ID, RATING_VALUE, REVIEW_VALUE);
        //when
        var result = this.currentProductController.createReview(PRODUCT_ID, badProductPayload, model, response);
        //then
        assertEquals("customer/products/current_product", result.block());
        assertEquals(badProductPayload, model.getAttribute("productReviewDetails"));
        assertEquals(errors, model.getAttribute("errors"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleNoSuchElementException_Return404ErrorPage() {
        final String ERROR_MESSAGE = "Product not found";
        final String ERROR_ATTRIBUTE_TITLE = "error";
        final String TEMPLATE_PATH = "customer/errors/no_such_element";

        //given
        var exception = new NoSuchElementException(ERROR_MESSAGE);
        var model = new ConcurrentModel();
        var httpResponse = new MockServerHttpResponse();
        //when
        var result = this.currentProductController.handleNoSuchElementException(exception, model, httpResponse);
        //then
        assertEquals(TEMPLATE_PATH, result);
        assertEquals(ERROR_MESSAGE, model.getAttribute(ERROR_ATTRIBUTE_TITLE));
        assertEquals(HttpStatus.NOT_FOUND, httpResponse.getStatusCode());
    }

    @Test
    void getCurrentPage_ReturnCurrentProductPage() {
        final List<ProductReview> reviews = getReviews();
        final NewProductPayload emptyPayload = new NewProductPayload(0, "");
        //given
        var model = new ConcurrentModel();

        Mockito.doReturn(Flux.fromIterable(reviews))
                .when(this.productReviewsClient).getProductReviewsByProductId(PRODUCT_ID);
        //when
        var result = this.currentProductController.getCurrentPage(PRODUCT_ID, model);
        //then
        assertEquals("customer/products/current_product", result.block());
        assertEquals(reviews, model.getAttribute("productReviews"));
        assertNotNull(reviews);
        assertEquals(emptyPayload, model.getAttribute("productReviewDetails"));
    }

    @Test
    void getFavouriteStatus_ProductFound_ReturnPositiveAnswer() {

        //given
        var favouriteProduct = new FavouriteProduct(UUID.fromString("1f726df1-9a3a-4690-9632-b48bc9bb37d1"), PRODUCT_ID);
        Mockito.doReturn(Mono.just(favouriteProduct))
                .when(this.favouriteProductsClient).findFavouriteProductByProductId(PRODUCT_ID);
        //when
        StepVerifier.create(this.currentProductController.getFavouriteStatus(PRODUCT_ID))
        //then
                .expectNext(true)
                .expectComplete()
                .verify();

        Mockito.verify(this.favouriteProductsClient).findFavouriteProductByProductId(PRODUCT_ID);
        Mockito.verifyNoMoreInteractions(this.favouriteProductsClient);
        Mockito.verifyNoInteractions(this.productsClient, this.productReviewsClient);

    }

    @Test
    void getFavouriteStatus_ProductNotFound_ReturnNegativeAnswer() {

        //given
        Mockito.doReturn(Mono.empty())
                .when(this.favouriteProductsClient).findFavouriteProductByProductId(PRODUCT_ID);
        //when
        StepVerifier.create(this.currentProductController.getFavouriteStatus(PRODUCT_ID))
                //then
                .expectNext(false)
                .expectComplete()
                .verify();

        Mockito.verify(this.favouriteProductsClient).findFavouriteProductByProductId(PRODUCT_ID);
        Mockito.verifyNoMoreInteractions(this.favouriteProductsClient);
        Mockito.verifyNoInteractions(this.productsClient, this.productReviewsClient);

    }

    @Test
    void loadProduct_getReactiveImplementationOfCurrentProduct() {
        final String PRODUCT_TITLE = "Test product";
        final String PRODUCT_DESCRIPTION = "Description of test product";

        //given
        var product = new Product(PRODUCT_ID, PRODUCT_TITLE, PRODUCT_DESCRIPTION);
        Mockito.doReturn(Mono.just(product)).when(this.productsClient).findProductById(PRODUCT_ID);

        //when
        StepVerifier.create(this.currentProductController.loadProduct(PRODUCT_ID))
        //then
                .expectNext(new Product(PRODUCT_ID, PRODUCT_TITLE, PRODUCT_DESCRIPTION))
                .expectComplete()
                .verify();

        Mockito.verify(this.productsClient).findProductById(PRODUCT_ID);
        Mockito.verifyNoMoreInteractions(this.productsClient);
        Mockito.verifyNoInteractions(this.favouriteProductsClient, this.productReviewsClient);
    }

}