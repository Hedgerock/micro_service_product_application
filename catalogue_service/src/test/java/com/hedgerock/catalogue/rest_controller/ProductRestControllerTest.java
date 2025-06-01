package com.hedgerock.catalogue.rest_controller;

import com.hedgerock.catalogue.entity.Product;
import com.hedgerock.catalogue.payload.UpdateProductPayload;
import com.hedgerock.catalogue.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Locale;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductRestControllerTest {
    @Mock
    ProductService productService;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ProductRestController productRestController;

    @Test
    void handleNoSuchElementException_ReturnResponseEntityWithProblemDetailsAndStatusNotFound() {
        var noSuchElementException = new NoSuchElementException("Product not found");
        var locale = Locale.getDefault();

        //given
        Mockito.doReturn("Bad Request details")
                .when(this.messageSource).getMessage(
                        noSuchElementException.getMessage(),
                        new Object[0],
                        noSuchElementException.getMessage(),
                        locale
                );
        //when
        var result = this.productRestController.handleNoSuchElementException(noSuchElementException, locale);

        //then
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getBody().getStatus());
        assertEquals("Bad Request details", result.getBody().getDetail());

        Mockito.verifyNoInteractions(this.productService);
    }

    @Test
    void deleteProduct() {
        var product = new Product(4L, "Product for delete test", "Details for delelete test");
        //given
        //when
        var result = this.productRestController.deleteProduct(product);
        //then
        assertEquals(ResponseEntity.noContent().build(), result);
    }

    @Test
    void updateProduct_RequestIsNotValid_ReturnNoContent() throws BindException {
        final var PRODUCT_ID = 3L;
        final var updateProductPayload = new UpdateProductPayload("", "Super long details");
        final BindingResult bindingResult = new BindException(updateProductPayload, "updatedPayloadExceptions");

        bindingResult.addError(new ObjectError("error1", "Can't be less than 3 symbols"));

        //given
        //when
        var result = assertThrows(BindException.class, () ->
                this.productRestController.updateProduct(PRODUCT_ID, updateProductPayload, bindingResult));
        //then

        assertEquals(result.getAllErrors(), bindingResult.getAllErrors());
    }

    @Test
    void updateProduct_RequestIsValid_ReturnNoContent() throws BindException {
        final var PRODUCT_ID = 3L;
        final var updateProductPayload = new UpdateProductPayload("Updated title", "Updated details");
        final BindingResult bindingResult = new BindException(updateProductPayload, "updatedPayloadExceptions");

        final var product = new Product(PRODUCT_ID, "Updated title", "Updated details");

        //given
        Mockito.doReturn(product)
                .when(this.productService).updateCurrentProduct(PRODUCT_ID, updateProductPayload);
        //when

        var result = this.productRestController.updateProduct(PRODUCT_ID, updateProductPayload, bindingResult);
        //then

        assertEquals(ResponseEntity.noContent().build(), result);

        Mockito.verify(this.productService).updateCurrentProduct(PRODUCT_ID, updateProductPayload);
        Mockito.verifyNoMoreInteractions(this.productService);
    }

    @Test
    void findProduct_ReturnCurrentProduct() {
        var product = new Product(2L, "Current product", "Current product details");
        //given
        //when
        var result = this.productRestController.findProduct(product);
        //then

        assertEquals(product, result);
    }

    @Test
    void getProduct_ProductNotFound_ThrowsNoSuchElementException() {
        final var PRODUCT_ID = 1L;
        final var noSuchElementException = new NoSuchElementException("catalogue.errors.product.not_found");
        //given
        Mockito.doReturn(null)
                .when(this.productService).findCurrentProduct(PRODUCT_ID);
        //when
        assertThrows(NoSuchElementException.class, () ->
                this.productRestController.getProduct(PRODUCT_ID));
        //then

        assertEquals("catalogue.errors.product.not_found", noSuchElementException.getMessage());
    }

    @Test
    void getProduct_ProductFound_ModelSetAttributeProduct() {
        final var PRODUCT_ID = 1L;
        var product = new Product(PRODUCT_ID, "Test product", "Test product details");
        //given
        Mockito.doReturn(product)
                .when(this.productService).findCurrentProduct(PRODUCT_ID);
        //when
        var result = this.productRestController.getProduct(PRODUCT_ID);
        //then

        assertEquals(product, result);

        Mockito.verify(this.productService).findCurrentProduct(PRODUCT_ID);
        Mockito.verifyNoMoreInteractions(this.productService);
    }
}