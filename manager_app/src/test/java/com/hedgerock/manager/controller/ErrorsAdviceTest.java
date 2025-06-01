package com.hedgerock.manager.controller;

import com.hedgerock.manager.exceptions.BadRequestException;
import com.hedgerock.manager.exceptions.ProductNotFoundException;
import com.hedgerock.manager.exceptions.WrongCredentialsException;
import com.hedgerock.manager.payload.NewProductPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ConcurrentModel;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ErrorsAdviceTest {

    @InjectMocks
    ErrorsAdvice errorsAdvice;

    @Test
    void whenBadRequest() {
        var badPayload = new NewProductPayload(null, "Super long message");
        var badRequestException = new BadRequestException(
                List.of("error1", "error2"),
                "redirect:/catalogue/products/list/1",
                badPayload
        );
        //given
        var redirectAttr = new RedirectAttributesModelMap();

        //when
        var result = this.errorsAdvice.whenBadRequest(badRequestException, redirectAttr);
        //then
        assertEquals("redirect:/catalogue/products/list/1", result);
        assertEquals(List.of("error1", "error2"), redirectAttr.getFlashAttributes().get("errors"));
        assertEquals(badPayload, redirectAttr.getFlashAttributes().get("prevDetails"));
    }

    @Test
    void whenCredentialsIsIncorrect() {
        var productPayload = new NewProductPayload("Test title", "Test details");
        BindingResult bindingResult = new BindException(productPayload, "productPayload");

        ObjectError firstError = new ObjectError("error1", "Error 1");
        ObjectError secondError = new ObjectError("error2", "Error 2");

        bindingResult.addError(firstError);
        bindingResult.addError(secondError);

        var credentialsIsIncorrect = new WrongCredentialsException(
                "Wrong credentials", "catalogue/products/list/1", bindingResult, productPayload
        );
        //given
        var redirectAttr = new RedirectAttributesModelMap();
        //when
        var result = this.errorsAdvice.whenCredentialsIsIncorrect(credentialsIsIncorrect, redirectAttr);
        //them
        assertEquals("redirect:/catalogue/products/list/1", result);
        assertNotNull(firstError.getDefaultMessage());
        assertNotNull(secondError.getDefaultMessage());
        assertEquals(
                List.of(firstError.getDefaultMessage(), secondError.getDefaultMessage()),
                redirectAttr.getFlashAttributes().get("errors")
        );
        assertEquals(productPayload, redirectAttr.getFlashAttributes().get("prevDetails"));
    }

    @Test
    void whenNoResourceFoundException() {
        var resourceNotFoundException = new NoResourceFoundException(HttpMethod.GET, "Resource not found");
        //given
        var redirectAttr = new RedirectAttributesModelMap();
        //when
        var result = this.errorsAdvice.whenResourceIsIncorrect(resourceNotFoundException, redirectAttr);
        //then

        assertEquals("redirect:/not-found-page", result);
        assertEquals(resourceNotFoundException, redirectAttr.getFlashAttributes().get("error"));
    }

    @Test
    void whenProductNotFound() {
        var productNotFoundException = new ProductNotFoundException("Product not found");
        //given
        var redirectAttr = new RedirectAttributesModelMap();
        //when
        var result = this.errorsAdvice.whenProductNotFound(productNotFoundException, redirectAttr);
        //then

        assertEquals("redirect:/not-found-page", result);
        assertEquals(productNotFoundException, redirectAttr.getFlashAttributes().get("error"));
    }

}