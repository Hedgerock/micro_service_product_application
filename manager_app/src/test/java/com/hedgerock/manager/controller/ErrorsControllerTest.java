package com.hedgerock.manager.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ErrorsControllerTest {

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ErrorsController errorsController;

    @Test
    void initNotFoundPage_WhenExceptionIsEmpty_RedirectToTheProductList() {

        //given
        var exception = new RuntimeException();
        var response = new MockHttpServletResponse();
        var model = new ConcurrentModel();
        var locale = Locale.getDefault();

        //when
        var result = this.errorsController.initNotFoundPage(exception, model, response, locale);
        //then
        assertEquals("redirect:/catalogue/products/list", result);
    }

    @Test
    void initNotFoundPage_WhenExceptionIsNotEmpty_ReturnErrorPage() {

        //given
        var exception = new RuntimeException("Product not found");
        var response = new MockHttpServletResponse();
        var model = new ConcurrentModel();
        var locale = Locale.getDefault();

        Mockito.doReturn("Product not found")
                .when(this.messageSource)
                    .getMessage(exception.getMessage(), new Object[0], exception.getMessage(), locale);

        //when
        var result = this.errorsController.initNotFoundPage(exception, model, response, locale);
        //then
        assertEquals("core", result);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("Product not found", model.getAttribute("error"));
        assertEquals("errors", model.getAttribute("pageFolderTitle"));
        assertEquals("Page not found", model.getAttribute("pageTitle"));
    }
}