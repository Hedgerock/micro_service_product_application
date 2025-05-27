package com.hedgerock.customer.controller;

import com.hedgerock.customer.client.FavouriteProductsClient;
import com.hedgerock.customer.client.ProductReviewsClient;
import com.hedgerock.customer.client.ProductsClient;
import com.hedgerock.customer.client.exception.BadRequestClientException;
import com.hedgerock.customer.controller.payload.NewProductPayload;
import com.hedgerock.customer.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("customer/products/list/{productId:\\d+}")
public class CurrentProductController {

    private final ProductsClient productsClient;
    private final ProductReviewsClient productReviewClient;
    private final FavouriteProductsClient favoriteProductClient;

    @ModelAttribute(name="product", binding = false)
    public Mono<Product> loadProduct(@PathVariable("productId") Long productId) {
        return this.productsClient.findProductById(productId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("customer.product.not_found")));
    }

    @ModelAttribute(name="isFavourite")
    public Mono<Boolean> getFavouriteStatus(@PathVariable("productId") Long productId) {
        return this.favoriteProductClient.findFavouriteProductByProductId(productId)
                .map(el -> true)
                .defaultIfEmpty(false);
    }

    @GetMapping
    public Mono<String> getCurrentPage(
            @PathVariable("productId") Long productId,
            Model model) {
        model.addAttribute("productReviewDetails", new NewProductPayload(0, ""));
        return this.productReviewClient.getProductReviewsByProductId(productId).collectList()
                .doOnNext(reviews -> model.addAttribute("productReviews", reviews))
                .thenReturn("customer/products/current_product");
    }


    @PostMapping("create-review")
    public Mono<String> createReview(
            @PathVariable("productId") Long productId,
            NewProductPayload newProductPayload,
            Model model,
            ServerHttpResponse response
    ) {
        return this.productReviewClient.addReview(productId, newProductPayload.rating(), newProductPayload.review())
                .thenReturn(String.format("redirect:/customer/products/list/%d", productId))
                .onErrorResume(BadRequestClientException.class, exception -> {
                    model.addAttribute("productReviewDetails", newProductPayload);
                    model.addAttribute("errors", exception.getErrors());
                    response.setStatusCode(HttpStatus.BAD_REQUEST);

                    return Mono.just("customer/products/current_product");
                }
        );
    }


    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model, ServerHttpResponse response) {
        model.addAttribute("error", exception.getMessage());
        response.setStatusCode(HttpStatus.NOT_FOUND);

        return "customer/errors/no_such_element";
    }

    @PostMapping("add-to-favourites")
    public Mono<String> addToFavourites(@ModelAttribute("product") Mono<Product> product) {

        return product.map(Product::id)
                .flatMap(productId ->
                        this.favoriteProductClient.addProductToFavourite(productId)
                                .thenReturn(String.format("redirect:/customer/products/list/%d", productId))
                                .onErrorResume(exception ->
                                        Mono.just("redirect:/customer/products/list/%d".formatted(productId)))
                );
    }

    @PostMapping("remove-from-favourites")
    public Mono<String> deleteFromFavourites(@ModelAttribute("product") Mono<Product> product) {

        return product.map(Product::id)
                .flatMap(productId ->
                        this.favoriteProductClient.removeProductFromFavourite(productId)
                                .thenReturn(String.format("redirect:/customer/products/list/%d", productId))
                                .onErrorResume(exception ->
                                        Mono.just("redirect:/customer/products/list/%d".formatted(productId)))
                );
    }

    @ModelAttribute
    public Mono<CsrfToken> loadCsrfToken(ServerWebExchange exchange) {
        Mono<CsrfToken> csrfTokenMono = exchange.getAttribute(CsrfToken.class.getName());

        if (csrfTokenMono == null) csrfTokenMono = Mono.empty();

        return csrfTokenMono
                .doOnSuccess(token ->
                        exchange.getAttributes().put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
    }
}
