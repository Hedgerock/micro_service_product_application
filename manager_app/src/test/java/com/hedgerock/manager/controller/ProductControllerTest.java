package com.hedgerock.manager.controller;

import com.hedgerock.manager.client.ProductRestClient;
import com.hedgerock.manager.entities.Product;
import com.hedgerock.manager.exceptions.BadRequestException;
import com.hedgerock.manager.payload.UpdateProductPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductRestClient productRestClient;

    @InjectMocks
    ProductController productController;


    @Test
    void deleteCurrentProduct() {
        final var PRODUCT_ID = 1L;
        final var PRODUCT = new Product(PRODUCT_ID, "Product title", "Product details");
        //given
        Mockito.doReturn(Optional.of(PRODUCT))
                .when(this.productRestClient).findProduct(PRODUCT_ID);

        var model = new ConcurrentModel();
        var redirectAttributes = new RedirectAttributesModelMap();
        //when
        var result = this.productController.deleteCurrentProduct(PRODUCT_ID, model, redirectAttributes);
        //then

        assertEquals("redirect:/catalogue/products/list", result);
        assertEquals(
                "Product %s has been deleted".formatted(PRODUCT.title()),
                redirectAttributes.getFlashAttributes().get("status"));

        Mockito.verify(this.productRestClient).findProduct(PRODUCT_ID);
        Mockito.verify(this.productRestClient).deleteProduct(PRODUCT_ID);

        Mockito.verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void initProduct_BadRequest_ThrowsBadRequestExceptionAndRedirectToTheEditPage() {
        final var PRODUCT_ID = 1L;
        final var UPDATE_PAYLOAD = new UpdateProductPayload(null, "Super long message (1000+ symbols)");
        final var REDIRECT_PATH = "redirect:/catalogue/products/edit/%d".formatted(PRODUCT_ID);

        //given
        var redirectAttributes = new RedirectAttributesModelMap();
        Mockito.doThrow(new BadRequestException(
                List.of("error1", "error2"),
                REDIRECT_PATH,
                UPDATE_PAYLOAD
        ))
                .when(this.productRestClient).updateProduct(PRODUCT_ID, UPDATE_PAYLOAD);
        //when
        //then
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                this.productController.updateCurrentProduct(PRODUCT_ID, UPDATE_PAYLOAD, redirectAttributes));

        assertEquals(REDIRECT_PATH, exception.getRedirectPath());
        assertEquals(List.of("error1", "error2"), exception.getErrors());
        assertEquals(UPDATE_PAYLOAD, exception.getProductPayload());

        Mockito.verify(this.productRestClient).updateProduct(PRODUCT_ID, UPDATE_PAYLOAD);
        Mockito.verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void initProduct_Success_RedirectToTheCreatedProductPage() {
        final var PRODUCT_ID = 1L;
        final var UPDATE_PAYLOAD = new UpdateProductPayload("Updated title", "Updated details");
        final var PRODUCT = new Product(PRODUCT_ID, UPDATE_PAYLOAD.title(), UPDATE_PAYLOAD.details());

        //given
        var redirectAttributes = new RedirectAttributesModelMap();
        Mockito.doReturn(PRODUCT)
                .when(this.productRestClient).updateProduct(PRODUCT_ID, UPDATE_PAYLOAD);
        //when
        var result = this.productController.updateCurrentProduct(PRODUCT_ID, UPDATE_PAYLOAD, redirectAttributes);
        //then
        assertEquals("redirect:/catalogue/products/list/1", result);
        assertEquals(
                "Current product has successfully updated",
                redirectAttributes.getFlashAttributes().get("status"));

        Mockito.verify(this.productRestClient).updateProduct(PRODUCT_ID, UPDATE_PAYLOAD);
        Mockito.verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void getCurrentProduct_ReturnToTheCurrentProductPage() {
        final var PRODUCT_ID = 1L;
        final var PRODUCT = new Product(PRODUCT_ID, "Product title", "Product details");

        //given
        Mockito.doReturn(Optional.of(PRODUCT))
                .when(this.productRestClient).findProduct(PRODUCT_ID);

        var model = new ConcurrentModel();
        var redirectAttributes = new RedirectAttributesModelMap();
        //when
        var result = this.productController.getCurrentProduct(PRODUCT_ID, redirectAttributes, model);
        //then
        assertEquals("core", result);
        assertEquals("%s - page".formatted(PRODUCT.title()), model.getAttribute("pageTitle"));

        Mockito.verify(this.productRestClient).findProduct(PRODUCT_ID);
        Mockito.verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    void createProduct_credentialsAreEmpty_ReturnCoreViewOfCreationPage() {
        final var ID = 1L;
        final var TITLE = "Test title";
        final var DETAILS = "Test details";
        final var PRODUCT = new Product(ID, TITLE, DETAILS);
        final var UPDATED_PRODUCT_PAYLOAD = new UpdateProductPayload(null, null);

        //given
        Mockito.doReturn(Optional.of(PRODUCT))
                .when(this.productRestClient).findProduct(ID);
        var model = new ConcurrentModel();
        var redirectAttributes = new RedirectAttributesModelMap();
        //when
        var result = this.productController.updateProduct(ID, UPDATED_PRODUCT_PAYLOAD, model, redirectAttributes);
        //then

        assertEquals("core", result);
        assertEquals(PRODUCT.getUpdatePayload(), model.getAttribute("newProduct"));
        assertEquals("%s update page".formatted(PRODUCT.title()), model.getAttribute("pageTitle"));
        assertEquals(ID, model.getAttribute("productId"));

        Mockito.verify(this.productRestClient).findProduct(ID);
        Mockito.verifyNoMoreInteractions(this.productRestClient);
    }


    @Test
    void createProduct_credentialsAreNotEmpty_ReturnCoreViewOfCreationPage() {
        final var ID = 1L;
        final var TITLE = "Test title";
        final var DETAILS = "Test details";
        final var PRODUCT = new Product(ID, TITLE, DETAILS);
        final var UPDATED_PRODUCT_PAYLOAD = new UpdateProductPayload("Updated title", "Updated details");

        //given
        Mockito.doReturn(Optional.of(PRODUCT))
                .when(this.productRestClient).findProduct(ID);
        var model = new ConcurrentModel();
        var redirectAttributes = new RedirectAttributesModelMap();
        //when
        var result = this.productController.updateProduct(ID, UPDATED_PRODUCT_PAYLOAD, model, redirectAttributes);
        //then

        assertEquals("core", result);
        assertEquals(UPDATED_PRODUCT_PAYLOAD, model.getAttribute("newProduct"));
        assertEquals("%s update page".formatted(PRODUCT.title()), model.getAttribute("pageTitle"));
        assertEquals(ID, model.getAttribute("productId"));

        Mockito.verify(this.productRestClient).findProduct(ID);
        Mockito.verifyNoMoreInteractions(this.productRestClient);
    }
}