package com.hedgerock.catalogue.rest_controller;

import com.hedgerock.catalogue.entity.Product;
import com.hedgerock.catalogue.payload.UpdateProductPayload;
import com.hedgerock.catalogue.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products/{productId:\\d+}")
public class ProductRestController {

    private final ProductService productService;
    private final MessageSource messageSource;

    @ModelAttribute("product")
    public Product getProduct(@PathVariable("productId") long productId) {
        Product product = this.productService.findCurrentProduct(productId);

        if (product == null) {
            throw new NoSuchElementException("catalogue.errors.product.not_found");
        }

        return product;
    }

    @GetMapping
    public Product findProduct(
        @ModelAttribute("product") Product product
    ) {
        return product;
    }

    @PatchMapping
    public ResponseEntity<Void> updateProduct(
            @PathVariable("productId") long productId,
            @Valid @RequestBody UpdateProductPayload productPayload,
            BindingResult result
    ) throws BindException {
        if (result.hasErrors()) {

            if (result instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(result);
            }

        }

        this.productService.updateCurrentProduct(productId, productPayload);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(
            @ModelAttribute("product") Product product
    ) {
        this.productService.removeProduct(product);

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(NoSuchElementException exception, Locale locale) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                this.messageSource.getMessage(
                        exception.getMessage(),
                        new Object[0],
                        exception.getMessage(),
                        locale
                )
        ));
    }
}
