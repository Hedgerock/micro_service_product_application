package com.hedgerock.customer.controller;

import com.hedgerock.customer.client.FavouriteProductsClient;
import com.hedgerock.customer.client.ProductsClient;
import com.hedgerock.customer.entity.FavouriteProduct;
import com.hedgerock.customer.entity.Product;
import com.hedgerock.customer.utils.body.ProductsCredentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProductsControllerTest {

    @Mock
    ProductsClient productsClient;

    @Mock
    FavouriteProductsClient favouriteProductsClient;

    @InjectMocks
    ProductsController productsController;

    @Test
    void getFavouriteProductsListPage_ContentFound_ReturnCurrentListView() {
        final String TITLE = "test";
        final List<Product> LIST_OF_PRODUCTS = ProductsCredentials.getProductsList();
        final List<FavouriteProduct> LIST_OF_FAVOURITE_PRODUCTS = ProductsCredentials.getFavouriteProductList();
        final List<Long> FAVOURITE_PRODUCTS_IDS =
                LIST_OF_FAVOURITE_PRODUCTS.stream().map(FavouriteProduct::productId).toList();

        final List<Product> ACTUAL_RESULT = LIST_OF_PRODUCTS.stream()
                .filter(product -> FAVOURITE_PRODUCTS_IDS.contains(product.id()))
                .toList();

        //given
        var model = new ConcurrentModel();

        Mockito.doReturn(Flux.fromIterable(LIST_OF_FAVOURITE_PRODUCTS))
                        .when(this.favouriteProductsClient).getFavouriteProducts();

        Mockito.doReturn(Flux.fromIterable(LIST_OF_PRODUCTS))
                .when(this.productsClient).findAllProducts(TITLE);

        //when
        var result = this.productsController.getFavouriteProductsPage(model, TITLE);
        //then
        assertEquals(TITLE, model.getAttribute("title"));
        assertEquals("customer/products/list", result.block());
        assertEquals(ACTUAL_RESULT, model.getAttribute("products"));
    }

    @Test
    void getFavouriteProductsListPage_ContentNotFound_ReturnCurrentListView() {
        final String TITLE = "test";

        //given
        var model = new ConcurrentModel();

        Mockito.doReturn(Flux.empty())
                .when(this.favouriteProductsClient).getFavouriteProducts();

        Mockito.doReturn(Flux.empty())
                .when(this.productsClient).findAllProducts(TITLE);

        //when
        var result = this.productsController.getFavouriteProductsPage(model, TITLE);
        //then
        assertEquals(TITLE, model.getAttribute("title"));
        assertEquals("customer/products/list", result.block());
        assertEquals(Collections.EMPTY_LIST, model.getAttribute("products"));

    }

    @Test
    void getProductsListPage_ContentFound_ReturnCurrentListView() {
        final String TITLE = "test";
        final List<Product> LIST_OF_PRODUCTS = ProductsCredentials.getProductsList();

        //given
        var model = new ConcurrentModel();

        Mockito.doReturn(Flux.fromIterable(LIST_OF_PRODUCTS))
                .when(this.productsClient).findAllProducts(TITLE);

        //when
        var result = this.productsController.getProductsListPage(model, TITLE);
        //then
        assertEquals(TITLE, model.getAttribute("title"));
        assertEquals("customer/products/list", result.block());
        assertEquals(LIST_OF_PRODUCTS, model.getAttribute("products"));

    }

    @Test
    void getProductsListPage_ContentNotFound_ReturnEmptyListView() {
        final String TITLE = "test";

        //given
        var model = new ConcurrentModel();

        Mockito.doReturn(Flux.empty())
                .when(this.productsClient).findAllProducts(TITLE);

        //when
        var result = this.productsController.getProductsListPage(model, TITLE);
        //then
        assertEquals(TITLE, model.getAttribute("title"));
        assertEquals("customer/products/list", result.block());
        assertEquals(Collections.EMPTY_LIST, model.getAttribute("products"));

    }
}
