package com.hedgerock.manager.controller;

import com.hedgerock.manager.client.ProductRestClient;
import com.hedgerock.manager.exceptions.BadRequestException;
import com.hedgerock.manager.payload.NewProductPayload;
import com.hedgerock.manager.entities.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Objects;

@Controller
@RequestMapping("catalogue/products")
@Slf4j
public class ProductsListController extends BatyaController {

    public ProductsListController(ProductRestClient productRestClient) {
        super(productRestClient);
    }

    @GetMapping("/list")
    public String getProductsList(
            Model model,
            @RequestParam(name = "title", required = false) String title
    ) {
        model.addAttribute("products", this.productRestClient.findAllProducts(title));
        model.addAttribute("title", title);
        setPageTitle(model, "Products list");


        return CORE_HTML;
    }

    @GetMapping("create")
    public String createProduct(
            @ModelAttribute("prevDetails") NewProductPayload productPayload,
            @ModelAttribute("errors") ArrayList<String> errors,
            Model model
    ) {
        model.addAttribute("newProduct", Objects.requireNonNullElseGet(productPayload,
                () -> new Product(-1, null, null).getUpdatePayload()));

        model.addAttribute("errors", errors);
        setPageTitle(model, "New product creation page");

        return CORE_HTML;
    }

    @PostMapping("create")
    public String createNewCurrentProduct(
            @ModelAttribute("newProduct") NewProductPayload newProductPayload,
            RedirectAttributes redirectAttributes
    ) {
        try {
            log.info("Method started: {}", newProductPayload);

            Product product = this.productRestClient
                    .createProduct(newProductPayload.title(), newProductPayload.details());
            redirectAttributes.addFlashAttribute(REDIRECT_TITLE, String.format("%s has successfully created", product.title()));
            return String.format("redirect:/catalogue/products/list/%d", product.id());
        } catch (BadRequestException exception) {
            throw new BadRequestException(
                    exception.getErrors(), "redirect:/catalogue/products/create", newProductPayload);
        }
    }
}
