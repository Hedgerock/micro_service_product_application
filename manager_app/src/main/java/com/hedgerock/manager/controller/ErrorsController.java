package com.hedgerock.manager.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Locale;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ErrorsController {
    private final MessageSource messageSource;

    private<E extends Throwable> void addExceptionAttr(E exception, Model model, Locale locale) {

        model.addAttribute(
                "error",
                this.messageSource.getMessage(exception.getMessage(), new Object[0], exception.getMessage(), locale)
        );
    }

    @GetMapping("/not-found-page")
    public<E extends Throwable> String initNotFoundPage(
            @ModelAttribute("error") E exception,
            Model model,
            HttpServletResponse response,
            Locale locale
    ) {

        if (exception.getMessage() == null) {
            return "redirect:/catalogue/products/list";
        }

        response.setStatus(HttpStatus.NOT_FOUND.value());
        addExceptionAttr(exception, model, locale);

        model.addAttribute("pageFolderTitle", "errors");
        model.addAttribute("pageTitle", "Page not found");

        return "core";
    }
}
