package com.hedgerock.manager.client;

import com.hedgerock.manager.entities.Product;
import com.hedgerock.manager.payload.NewProductPayload;
import com.hedgerock.manager.payload.UpdateProductPayload;

import java.util.List;
import java.util.Optional;

public interface ProductRestClient {
    List<Product> findAllProducts(String title);
    Product createProduct(String title, String details);

    Optional<Product> findProduct(Long id);

    Product updateProduct(long id, UpdateProductPayload productPayload);
    void deleteProduct(long id);
}
