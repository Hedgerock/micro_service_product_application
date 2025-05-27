package com.hedgerock.manager.controller;

import com.hedgerock.manager.client.ProductRestClient;
import com.hedgerock.manager.entities.Product;
import com.hedgerock.manager.exceptions.BadRequestException;
import com.hedgerock.manager.payload.UpdateProductPayload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/catalogue/products")
public class ProductController extends BatyaController {

    public ProductController(ProductRestClient productRestClient) {
        super(productRestClient);
    }

    @GetMapping("/edit/{id:\\d+}")
    public String createProduct(
            @PathVariable Long id,
            @ModelAttribute("prevDetails") UpdateProductPayload productPayload,
            Model model,
            RedirectAttributes attributes
    ) {
        Product product = initProduct(id, model, attributes, false);

        if (productPayload.getTitle() != null) {
            model.addAttribute("newProduct", productPayload);
        } else {
            UpdateProductPayload currentPayload = product.getUpdatePayload();
            model.addAttribute("newProduct", currentPayload);
        }

        setPageTitle(model, String.format("%s update page", product.title()));
        model.addAttribute("productId", id);

        return CORE_HTML;
    }

    @GetMapping("/list/{id:\\d+}")
    public String getCurrentProduct(@PathVariable Long id, RedirectAttributes attributes, Model model) {
        Product product = initProduct(id, model, attributes);
        setPageTitle(model, String.format("%s - page", product.title()));

        return CORE_HTML;
    }


    @PostMapping("/update/{id:\\d+}")
    public String initProduct(
            @PathVariable Long id,
            @ModelAttribute("newProduct") UpdateProductPayload updateProductPayload,
            RedirectAttributes attributes
    ) {
        try {
            Product product = this.productRestClient.updateProduct(id, updateProductPayload);
            attributes.addFlashAttribute(REDIRECT_TITLE, String.format("%s has successfully update", product.title()));

            return REDIRECT_TO_LIST;
        } catch (BadRequestException exception) {
            throw new BadRequestException(
                    exception.getErrors(), String.format("redirect:/catalogue/products/edit/%d", id), updateProductPayload);
        }
    }

    @PostMapping("/delete/{id:\\d+}")
    public String deleteCurrentProduct(
            @PathVariable Long id,
            Model model,
            RedirectAttributes attributes
    ) {

        Product product = initProduct(id, model, attributes, false);

        this.productRestClient.deleteProduct(id);

        attributes.addFlashAttribute(REDIRECT_TITLE, String.format("Product %s has been deleted", product.title()));

        return REDIRECT_TO_LIST;
    }
}
