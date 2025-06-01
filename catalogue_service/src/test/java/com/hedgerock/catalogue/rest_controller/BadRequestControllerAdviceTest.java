package com.hedgerock.catalogue.rest_controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BadRequestControllerAdviceTest {
    private static final String BAD_REQUEST_MESSAGE = "errors.400.title";

    @Mock
    MessageSource messageSource;

    @InjectMocks
    BadRequestControllerAdvice badRequestControllerAdvice;


    @Test
    void handleBadCredentialsException() {
        final BindException bindingResult = new BindException(new Object(), "errors");
        bindingResult.addError(new ObjectError("error1", "Invalid credentials"));
        final var locale = Locale.getDefault();
        //given
        Mockito.doReturn("Error details")
                .when(this.messageSource).getMessage(
                        BAD_REQUEST_MESSAGE,
                        new Object[0],
                        BAD_REQUEST_MESSAGE,
                        locale
                );
        //when
        var result = this.badRequestControllerAdvice.handleBadCredentialsException(bindingResult, locale);
        //then

        assertNotNull(result);
        assertNotNull(result.getBody());
        assertNotNull(result.getBody().getProperties());

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getBody().getStatus());

        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals(List.of("Invalid credentials"), result.getBody().getProperties().get("errors"));

    }

}