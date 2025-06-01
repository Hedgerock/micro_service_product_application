package com.hedgerock.catalogue.rest_controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;
import java.util.Random;

@ControllerAdvice
@RequiredArgsConstructor
public class BadRequestControllerAdvice {
    private static final String BAD_REQUEST_MESSAGE = "errors.400.title";

    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentialsException(
            BindException exception,
            Locale locale
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                this.messageSource.getMessage(
                        BAD_REQUEST_MESSAGE,
                        new Object[0],
                        BAD_REQUEST_MESSAGE,
                        locale
                )
        );

        problemDetail.setProperty(
                "errors",
                exception.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList());

        return ResponseEntity.badRequest().body(problemDetail);
    }


}
