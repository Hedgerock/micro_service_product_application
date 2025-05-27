package com.hedgerock.manager.controller;

import com.hedgerock.manager.client.ProductRestClient;
import com.hedgerock.manager.entities.Product;
import com.hedgerock.manager.exceptions.BadRequestException;
import com.hedgerock.manager.payload.NewProductPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @DisplayName("Method createProduct will create new Product and redirect the user to the product page after successful operation")
    void createProduct_RequestIsValid_ReturnsRedirectionToProductPage() {
        //given
        var payload = new NewProductPayload("New product", "descriptionOfNewProduct");

        doReturn(new Product(1, "New product", "descriptionOfNewProduct"))
                .when(this.productRestClient).createProduct(payload);

        //when
        var result = this.controller.initProduct(payload, redirectAttributes);

        //then
        assertEquals("redirect:/catalogue/products/list/1", result);
        verify(this.productRestClient).createProduct(payload);
        verifyNoMoreInteractions(this.productRestClient);
    }


    @Test
    @DisplayName("When user gives wrong credentials")
    void createProduct_RequestIsNotValid_ReturnRedirectToTheSamePage() {
        var payload = new NewProductPayload("  ", null);

        doThrow(new BadRequestException(List.of("error1", "error2")))
                .when(this.productRestClient)
                .createProduct(payload);

        assertThrows(BadRequestException.class, () -> this.controller.initProduct(payload, redirectAttributes));

        verify(this.productRestClient).createProduct(payload);
        verifyNoMoreInteractions(this.productRestClient);
    }
}