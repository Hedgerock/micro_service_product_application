package com.hedgerock.catalogue.rest_controller;

import com.hedgerock.catalogue.entity.Product;
import com.hedgerock.catalogue.payload.NewProductPayload;
import com.hedgerock.catalogue.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ProductsRestController {

    private final ProductService productService;

    @GetMapping
    @Operation(
            security = @SecurityRequirement(name = "keycloak")
    )
    public Iterable<Product> findProducts(
            @RequestParam(value = "title", required = false) String title
    ) {
        return this.productService.findAllProducts(title);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            security = @SecurityRequirement(name = "keycloak"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    type = "Object",
                                    properties = {
                                            @StringToClassMapItem(key = "title", value = String.class),
                                            @StringToClassMapItem(key = "details", value = String.class)
                                    }
                            )
                    )
            ),
            responses = {
                @ApiResponse(
                    responseCode = "201",
                    headers = @Header(name = "Content-Type", description = "Data type"),
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                            type = "Object",
                                            properties = {
                                                    @StringToClassMapItem(key = "id", value = Long.class),
                                                    @StringToClassMapItem(key = "title", value = String.class),
                                                    @StringToClassMapItem(key = "details", value = String.class)
                                            }
                                    )
                            )
                    }
            )
    })
    public ResponseEntity<Product> createProduct(
        @Valid @RequestBody NewProductPayload payload,
        BindingResult result,
        UriComponentsBuilder builder
    ) throws BindException {
        log.info("Result has errors: {}", result.hasErrors());

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
