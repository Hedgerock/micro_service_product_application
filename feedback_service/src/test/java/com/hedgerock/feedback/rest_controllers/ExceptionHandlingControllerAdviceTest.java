package com.hedgerock.feedback.rest_controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlingControllerAdviceTest {

    @Mock
    BindingResult bindingResult;

    @InjectMocks
    WebExchangeBindException webExchangeBindException;

    @InjectMocks
    ExceptionHandlingControllerAdvice controllerAdvice;

    @Test
    void handleWebExchangeBindException() {
        final var PROBLEM_DETAIL = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        PROBLEM_DETAIL.setProperty("errors", Collections.EMPTY_LIST);

        final var RESPONSE_ENTITY = ResponseEntity
                .badRequest().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(PROBLEM_DETAIL);
        //given
        //when
        StepVerifier.create(this.controllerAdvice.handleWebExchangeBindException(this.webExchangeBindException))
        //then
                .expectNext(RESPONSE_ENTITY)
                .verifyComplete();
    }
}