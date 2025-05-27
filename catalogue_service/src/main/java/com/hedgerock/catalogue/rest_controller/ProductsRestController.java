package com.hedgerock.catalogue.rest_controller;

import com.hedgerock.catalogue.entity.Product;
import com.hedgerock.catalogue.payload.NewProductPayload;
import com.hedgerock.catalogue.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products")
public class ProductsRestController {

    private final ProductService productService;

    @GetMapping
    public Iterable<Product> findProducts(
            @RequestParam(value = "title", required = false) String title
    ) {
        return this.productService.findAllProducts(title);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> createProduct(
        @Valid @RequestBody NewProductPayload payload,
        BindingResult result,
        UriComponentsBuilder builder
    ) throws BindException {
        if (result.hasErrors()) {
            if (result instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(result);
            }
        }

        Product product = this.productService.setNewProduct(payload);

        return ResponseEntity
                .created(builder
                        .replacePath("/catalogue-api/products/{productId}")
                        .build(Map.of("productId", product.getId()))
                )
                .body(product);
    }

}
