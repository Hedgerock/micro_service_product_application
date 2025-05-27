package com.hedgerock.manager.client;

import com.hedgerock.manager.entities.Product;
import com.hedgerock.manager.exceptions.BadRequestException;
import com.hedgerock.manager.exceptions.ProductNotFoundException;
import com.hedgerock.manager.payload.NewProductPayload;
import com.hedgerock.manager.payload.UpdateProductPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
public class ProductRestClientImpl implements ProductRestClient {
    private static final ParameterizedTypeReference<List<Product>> PRODUCTS_TYPE_REFERENCE = new ParameterizedTypeReference<>() {};
    private static final String DEFAULT_API_PATH = "/catalogue-api/products";

    private final RestClient restClient;

    @Override
    public List<Product> findAllProducts(String title) {
        return this.restClient
                .get()
                .uri(DEFAULT_API_PATH + "?title={title}", title)
                .retrieve()
                .body(PRODUCTS_TYPE_REFERENCE);

    }

    @Override
    public Product createProduct(NewProductPayload productPayload) {
        try {
            return this.restClient
                    .post()
                    .uri(DEFAULT_API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(productPayload)
                    .retrieve()
                    .body(Product.class);
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public Optional<Product> findProduct(Long id) {
        try {
            return Optional.ofNullable(
                    this.restClient
                            .get()
                            .uri(DEFAULT_API_PATH + "/{productId}", id)
                            .retrieve()
                            .body(Product.class)
            );
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ProductNotFoundException("catalogue.errors.product.not_found");
        }
    }

    @Override
    public Product updateProduct(long id, UpdateProductPayload productPayload) {
        try {
            return this.restClient
                    .patch()
                    .uri(DEFAULT_API_PATH + "/{productId}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(productPayload)
                    .retrieve()
                    .body(Product.class);
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public void deleteProduct(long id) {
        try {
            this.restClient.delete().uri(DEFAULT_API_PATH + "/{productId}", id).retrieve().toBodilessEntity();
        } catch (NoSuchElementException exception) {
            throw new NoSuchElementException(exception);
        }
    }
}
