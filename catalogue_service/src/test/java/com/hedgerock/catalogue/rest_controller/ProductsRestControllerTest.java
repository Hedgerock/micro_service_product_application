package com.hedgerock.catalogue.rest_controller;

import com.hedgerock.catalogue.entity.Product;
import com.hedgerock.catalogue.payload.NewProductPayload;
import com.hedgerock.catalogue.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProductsRestControllerTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductsRestController productsRestController;

    @Test
    void createProduct_RequestIsNotValid_ReturnCreatedProductResponseBody() {
        var productPayload = new NewProductPayload(null, "Super long details");
        BindingResult bindException = new BindException(productPayload, "Product payload errors");
        bindException.addError(new ObjectError("error1", "Error 1"));
        var uriComponentsBuilder = UriComponentsBuilder.newInstance();

        // given

        // when & then
        assertThrows(
                BindException.class,
                () -> this.productsRestController.createProduct(productPayload, bindException, uriComponentsBuilder));
    }


    @Test
    void createProduct_RequestIsValid_ReturnCreatedProductResponseBody() throws Exception {
        var productPayload = new NewProductPayload("Title", "Details");
        BindingResult bindingResult = new BindException(productPayload, "Product payload errors");
        var uriComponentsBuilder = UriComponentsBuilder.newInstance();
        var product = new Product(1L, "Title", "Details");

        //given
        Mockito.doReturn(product)
                .when(this.productService).setNewProduct(productPayload);
        //when
        var result = this.productsRestController.createProduct(productPayload, bindingResult, uriComponentsBuilder);
        //then
        assertEquals(
                ResponseEntity.created(uriComponentsBuilder
                                .replacePath("/catalogue-api/products/{productId}")
                                .build(Map.of("productId", product.getId()))
                        )
                        .body(product),
                result);

        Mockito.verify(this.productService).setNewProduct(productPayload);
        Mockito.verifyNoMoreInteractions(this.productService);

    }

    @Test
    void findProducts() {
        var products = List.of(
                new Product(1L, "First product", "First product details"),
                new Product(2L, "Second product", "Second product details")
        );

        //given
        Mockito.doReturn(products)
                .when(this.productService).findAllProducts("product");
        //when
        var result = this.productsRestController.findProducts("product");
        //then

        assertEquals(products, result);

        Mockito.verify(this.productService).findAllProducts("product");
        Mockito.verifyNoMoreInteractions(this.productService);
    }

}