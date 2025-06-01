package com.hedgerock.manager.controller;

import com.hedgerock.manager.client.ProductRestClient;
import com.hedgerock.manager.entities.Product;
import com.hedgerock.manager.exceptions.BadRequestException;
import com.hedgerock.manager.payload.NewProductPayload;
import com.hedgerock.manager.payload.UpdateProductPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Module tests for ProductsListController")
class ProductsListControllerTest {

    @Mock
    ProductRestClient productRestClient;

    @Mock
    RedirectAttributes redirectAttributes;

    //Or productRestClient = Mockito.mock(ProductRestClient.class);

    @InjectMocks
    ProductsListController controller;

    @Test
    void createProduct_WithEmptyCredentials_ReturnCore() {
        //given
        var model = new ConcurrentModel();
        //when
        var result = this.controller.createProduct(null, null, model);
        //then

        assertEquals("core", result);
        assertEquals(new UpdateProductPayload(null, null), model.getAttribute("newProduct"));
        assertNull(model.getAttribute("errors"));
        assertEquals("New product creation page", model.getAttribute("pageTitle"));
    }

    @Test
    void createProduct_WithNotEmptyProductPayloadAndErrors_ReturnCore() {
        var productPayload = new NewProductPayload("Test title", "Test details");
        var errors = new ArrayList<>(List.of("Error 1", "Error2"));
        //given
        var model = new ConcurrentModel();
        //when
        var result = this.controller.createProduct(productPayload, errors, model);
        //then

        assertEquals("core", result);
        assertEquals(productPayload, model.getAttribute("newProduct"));
        assertEquals(errors, model.getAttribute("errors"));
        assertEquals("New product creation page", model.getAttribute("pageTitle"));
    }

    @Test
    void getProductsList() {
        final var TITLE = "test";
        final var PRODUCTS = IntStream.range(0, 3).mapToObj(i ->
                new Product(i, "Test product #%d".formatted(i + 1), "Product detail #%d".formatted(i + 1))).toList();
        //given
        var model = new ConcurrentModel();

        Mockito.doReturn(PRODUCTS)
                .when(this.productRestClient).findAllProducts(TITLE);
        //when
        var result = this.controller.getProductsList(model, TITLE);
        //then
        assertEquals("core", result);
        assertEquals(PRODUCTS, model.getAttribute("products"));
        assertEquals(TITLE, model.getAttribute("title"));
        assertEquals("Products list", model.getAttribute("pageTitle"));

        Mockito.verify(this.productRestClient).findAllProducts(TITLE);
        Mockito.verifyNoMoreInteractions(this.productRestClient);
    }

    @Test
    @DisplayName("Method createProduct will create new Product and redirect the user to the product page after successful operation")
    void createProduct_RequestIsValid_ReturnsRedirectionToProductPage() {
        //given
        var payload = new NewProductPayload("New product", "descriptionOfNewProduct");

        doReturn(new Product(1, "New product", "descriptionOfNewProduct"))
                .when(this.productRestClient).createProduct("New product", "descriptionOfNewProduct");

        //when
        var result = this.controller.createNewCurrentProduct(payload, redirectAttributes);

        //then
        assertEquals("redirect:/catalogue/products/list/1", result);
        verify(this.productRestClient).createProduct("New product", "descriptionOfNewProduct");
        verifyNoMoreInteractions(this.productRestClient);
    }


    @Test
    @DisplayName("When user gives wrong credentials")
    void createProduct_RequestIsNotValid_ReturnRedirectToTheSamePage() {
        var payload = new NewProductPayload("  ", null);

        doThrow(new BadRequestException(List.of("error1", "error2")))
                .when(this.productRestClient)
                .createProduct("  ", null);

        assertThrows(BadRequestException.class, () -> this.controller.createNewCurrentProduct(payload, redirectAttributes));

        verify(this.productRestClient).createProduct("  ", null);
        verifyNoMoreInteractions(this.productRestClient);
    }
}