package com.hedgerock.manager.controller;

import com.hedgerock.manager.exceptions.BadRequestException;
import com.hedgerock.manager.exceptions.ProductNotFoundException;
import com.hedgerock.manager.exceptions.WrongCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;

@ControllerAdvice
@RequiredArgsConstructor
public class ErrorsAdvice {
    private static final String REDIRECT_TO_NOT_FOUND_PAGE = "redirect:/not-found-page";

    @ExceptionHandler(ProductNotFoundException.class)
    public String whenProductNotFound(
            ProductNotFoundException exception,
            RedirectAttributes attributes
    ) {
        attributes.addFlashAttribute("error", exception);

        return REDIRECT_TO_NOT_FOUND_PAGE;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String whenResourceIsIncorrect(
            NoResourceFoundException exception,
            RedirectAttributes attributes
    ) {
        attributes.addFlashAttribute("error", exception);

        return REDIRECT_TO_NOT_FOUND_PAGE;
    }

    @ExceptionHandler(WrongCredentialsException.class)
    public String whenCredentialsIsIncorrect(
            WrongCredentialsException exception,
            RedirectAttributes attributes
    ) {

        attributes.addFlashAttribute("errors", exception.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).toList());
        attributes.addFlashAttribute("prevDetails", exception.getProductPayload());

        return String.format("redirect:/%s", exception.getRedirectPath());
    }

    @ExceptionHandler(BadRequestException.class)
    public String whenBadRequest(
            BadRequestException exception,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("prevDetails", exception.getProductPayload());
        redirectAttributes.addFlashAttribute("errors", new ArrayList<>(exception.getErrors()));

        return exception.getRedirectPath();
    }
}
