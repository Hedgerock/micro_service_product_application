package com.hedgerock.customer.utils.body;

import com.hedgerock.customer.entity.FavouriteProduct;
import com.hedgerock.customer.entity.Product;
import com.hedgerock.customer.entity.ProductReview;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
public class ProductsCredentials {
    private static final Long PRODUCT_ID = 1L;
    private static final String PRODUCT_TITLE = "Test product";
    private static final String PRODUCT_DETAILS = "Test product details";


    public static String getProductsBodyJSONFormat() {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("id", PRODUCT_ID);
            jsonObject.put("title", PRODUCT_TITLE);
            jsonObject.put("details", PRODUCT_DETAILS);

            return jsonObject.toString();

        } catch (JSONException exception) {
            throw new RuntimeException(initException(exception));
        }
    }

    public static String getFavouriteProductId() {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("productId", PRODUCT_ID);

            return jsonObject.toString();

        } catch (JSONException exception) {
            throw new RuntimeException(initException(exception));
        }
    }

    public static String getFavouriteProductId(boolean withFavouriteId) {
        if (!withFavouriteId) {
            return getFavouriteProductId();
        }

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("id", "d046f920-f0eb-4dec-9ef3-1d30c3691222");
            jsonObject.put("productId", PRODUCT_ID);

            return jsonObject.toString();

        } catch (JSONException exception) {
            throw new RuntimeException(initException(exception));
        }
    }

    private static<E extends Throwable> String initException(E exception) {
        log.error(exception.getMessage(), exception);
        return "Something went wrong";
    }

    public static List<ProductReview> getReviews() {
        final ProductReview firstProductReview =
                new ProductReview(
                        UUID.fromString("c80af2ba-6daa-49ba-a2bb-3eb9a006b2e9"), PRODUCT_ID, 3, "Not good, not bad");

        final ProductReview secondProductReview =
                new ProductReview(
                        UUID.fromString("d84d16e4-ea52-4b5b-88d2-ef758560511c"), PRODUCT_ID, 5, "Super product");

        return List.of(firstProductReview, secondProductReview);
    }

    public static List<FavouriteProduct> getFavouriteProductList() {
        final List<FavouriteProduct> favouriteProducts = new ArrayList<>();

        IntStream.range(0, 2).forEach(i -> {
            FavouriteProduct favouriteProduct = new FavouriteProduct(UUID.randomUUID(), i);
            favouriteProducts.add(favouriteProduct);
        });

        return favouriteProducts;
    }

    public static List<Product> getProductsList() {
        final List<Product> products = new ArrayList<>();

        IntStream.range(0, 3).forEach(i -> {
            products.add(
                    new Product(
                            (long) i,
                            String.format("Test product #%d", i + 1),
                            String.format("Product detail %d", i + 1)
                    )
            );
        });

        return products;
    }

}
