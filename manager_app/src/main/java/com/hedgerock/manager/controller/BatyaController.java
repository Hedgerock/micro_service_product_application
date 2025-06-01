package com.hedgerock.manager.controller;

import com.hedgerock.manager.client.ProductRestClient;
import com.hedgerock.manager.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class BatyaController {
    protected static final String CORE_HTML = "core";
    protected final ProductRestClient productRestClient;
    protected static final String REDIRECT_TITLE = "status";
    protected static final String REDIRECT_TO_LIST = "redirect:/catalogue/products/list";

    @ModelAttribute("pageFolderTitle")
    public String setPageFolderTitle() {
        return "catalogue";
    }

    protected void setPageTitle(Model model, String value) {
        model.addAttribute("pageTitle", value);
    }

    protected Product initProduct(Long id, Model model, RedirectAttributes attributes) {
        return initProduct(id, model, attributes, true);
    }

    protected Product initProduct(Long id, Model model, RedirectAttributes attributes, boolean isAttributeRequired) {
        Product product = this.productRestClient.findProduct(id).get();

        if (isAttributeRequired) {
            model.addAttribute("currentProduct", product);
        }

        return product;
    }
}
