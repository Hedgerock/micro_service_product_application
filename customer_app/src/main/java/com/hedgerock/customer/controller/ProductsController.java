package com.hedgerock.customer.controller;

import com.hedgerock.customer.client.FavouriteProductsClient;
import com.hedgerock.customer.client.ProductsClient;
import com.hedgerock.customer.entity.FavouriteProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products")
public class ProductsController {

    private final ProductsClient productsClient;
    private final FavouriteProductsClient favouriteProductsClient;

    @GetMapping("list")
    public Mono<String> getProductsListPage(
            Model model,
            @RequestParam(value = "title", required = false) String title
    ) {
        model.addAttribute("title", title);

        return this.productsClient.findAllProducts(title)
                .collectList()
                .doOnNext(products -> model.addAttribute("products", products))
                .thenReturn("customer/products/list");
    }

    @GetMapping("favourites")
    public Mono<String> getFavouriteProductsPage(
            Model model,
            @RequestParam(value = "title", required = false) String title
    ) {
        model.addAttribute("title", title);

        return this.favouriteProductsClient.getFavouriteProducts()
                .map(FavouriteProduct::productId)
                .collectList()
                .flatMap(favouriteProducts -> this.productsClient.findAllProducts(title).filter(product ->
                            favouriteProducts.contains(product.id())).collectList())
                .doOnNext(products -> model.addAttribute("products", products))
                .thenReturn("customer/products/list");
    }
}
