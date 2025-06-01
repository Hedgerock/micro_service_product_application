package com.hedgerock.catalogue.service;

import com.hedgerock.catalogue.entity.Product;
import com.hedgerock.catalogue.interfaces.ProductPayload;
import com.hedgerock.catalogue.payload.NewProductPayload;
import com.hedgerock.catalogue.payload.UpdateProductPayload;
import com.hedgerock.catalogue.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    DefaultProductService defaultProductService;

    @Test
    void deleteProduct() {
        var product = new Product(2L, "Product for delete", "Details");

        this.defaultProductService.removeProduct(product);

        assertNotNull(product);
    }

    @Test
    void updateCurrentProduct_ReturnUpdatedValueOfProduct() {
        final var PRODUCT_ID = 2L;
        final var productPayload = new UpdateProductPayload("Updated title", "Updated details");
        final var product = new Product(2L, "Title", "Details");
        final var updatedProduct = new Product(2L, "Updated Title", "Updated details");
        //given
        Mockito.doReturn(Optional.of(product))
                .when(this.productRepository).findById(PRODUCT_ID);

        Mockito.doReturn(updatedProduct)
                .when(this.productRepository).save(product);
        //when
        var result = this.defaultProductService.updateCurrentProduct(PRODUCT_ID, productPayload);
        //then
        assertNotNull(result);
        assertInstanceOf(Product.class, result);
        assertEquals(updatedProduct, result);

        Mockito.verify(this.productRepository).findById(PRODUCT_ID);
        Mockito.verify(this.productRepository).save(product);
        Mockito.verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void findCurrentProduct_ProductNotFound_ThrowNoSuchElementException() {
        //given
        Mockito.doReturn(Optional.empty())
                .when(this.productRepository).findById(1L);
        //when
        var result = assertThrows(NoSuchElementException.class, () ->
                this.defaultProductService.findCurrentProduct(1L));
        //then

        assertEquals("catalogue.errors.product.not_found", result.getMessage());

        Mockito.verify(this.productRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void findCurrentProduct_ProductFound_ReturnProduct() {
        final var PRODUCT_ID = 1L;
        var product = new Product(PRODUCT_ID, "Product", "Details");
        //given
        Mockito.doReturn(Optional.of(product))
                .when(this.productRepository).findById(PRODUCT_ID);
        //when
        var result = this.defaultProductService.findCurrentProduct(PRODUCT_ID);
        //then
        assertNotNull(product);
        assertInstanceOf(Product.class, result);
        assertEquals(product, result);

        Mockito.verify(this.productRepository).findById(PRODUCT_ID);
        Mockito.verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void setNewProduct_ReturnNewProduct() {
        final ProductPayload productPayload = new NewProductPayload("New product", "New product details");
        final var product = new Product(1L, productPayload.getTitle(), productPayload.getDetails());

        //given
        Mockito.doReturn(product)
                .when(this.productRepository)
                .save(new Product(null, productPayload.getTitle(), productPayload.getDetails()));
        //when
        var result = this.defaultProductService.setNewProduct(productPayload);
        //then

        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    void findAllProducts_WhenTitleIsBlank_ReturnProducts() {

        final var products = IntStream.range(0, 3).mapToObj(i -> new Product(
                (long) i,
                String.format("product %d", i + 1),
                String.format("details number %d", i + 1)
        )).toList();

        //given
        Mockito.doReturn(products)
                .when(this.productRepository).findAll();
        //when
        var result = this.defaultProductService.findAllProducts("");
        //then

        assertEquals(products, result);

        Mockito.verify(this.productRepository).findAll();
        Mockito.verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void findAllProducts_WhenTitleIsNotPresent_ReturnProducts() {

        final var products = IntStream.range(0, 3).mapToObj(i -> new Product(
                (long) i,
                String.format("product %d", i + 1),
                String.format("details number %d", i + 1)
        )).toList();

        //given
        Mockito.doReturn(products)
                .when(this.productRepository).findAll();
        //when
        var result = this.defaultProductService.findAllProducts(null);
        //then

        assertEquals(products, result);

        Mockito.verify(this.productRepository).findAll();
        Mockito.verifyNoMoreInteractions(this.productRepository);
    }

    @Test
    void findAllProducts_WhenTitleIsPresent_ReturnFoundedProducts() {
        final String title = "Sea";

        final var products = IntStream.range(0, 3).mapToObj(i -> new Product(
                (long) i,
                String.format("Searched product %d", i + 1),
                String.format("Product details number %d", i + 1)
        )).toList();

        //given
        Mockito.doReturn(products)
                .when(this.productRepository).findAllByTitleLikeIgnoreCase("%" + title + "%");
        //when
        var result = this.defaultProductService.findAllProducts(title);
        //then

        assertEquals(products, result);

        Mockito.verify(this.productRepository).findAllByTitleLikeIgnoreCase("%" + title + "%");
        Mockito.verifyNoMoreInteractions(this.productRepository);
    }

}