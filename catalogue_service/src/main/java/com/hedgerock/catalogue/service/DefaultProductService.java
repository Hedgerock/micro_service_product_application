package com.hedgerock.catalogue.service;

import com.hedgerock.catalogue.entity.Product;
import com.hedgerock.catalogue.payload.UpdateProductPayload;
import com.hedgerock.catalogue.repository.ProductRepository;
import com.hedgerock.catalogue.interfaces.ProductPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {

    private final ProductRepository repository;

    @Override
    public Iterable<Product> findAllProducts(String title) {
        return title != null && !title.isBlank()
                ? this.repository.findAllByTitleLikeIgnoreCase("%" + title + "%")
                : this.repository.findAll();
    }

    @Override
    @Transactional
    public<T extends ProductPayload> Product setNewProduct(T payload) {
        return this.repository.save(new Product(null, payload.getTitle(), payload.getDetails()));
    }

    @Override
    public Product findCurrentProduct(Long id) {
        return this.repository.findById(id).orElseThrow(
                () -> new NoSuchElementException("catalogue.errors.product.not_found"));
    }

    @Override
    @Transactional
    public Product updateCurrentProduct(Long id, UpdateProductPayload updateProductPayload) {
        Product product = findCurrentProduct(id);
        product.setTitle(updateProductPayload.getTitle());
        product.setDetails(updateProductPayload.getDetails());

        return this.repository.save(product);
    }

    @Override
    @Transactional
    public void removeProduct(Product product) {
        this.repository.delete(product);
    }
}
