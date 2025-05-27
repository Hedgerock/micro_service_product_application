package com.hedgerock.catalogue.service;

import com.hedgerock.catalogue.entity.Product;
import com.hedgerock.catalogue.payload.UpdateProductPayload;
import com.hedgerock.catalogue.interfaces.ProductPayload;


public interface ProductService {
    Iterable<Product> findAllProducts(String title);
    <T extends ProductPayload>Product setNewProduct(T payload);
    Product findCurrentProduct(Long id);
    Product updateCurrentProduct(Long id, UpdateProductPayload newProductPayload);
    void removeProduct(Product product);
}
