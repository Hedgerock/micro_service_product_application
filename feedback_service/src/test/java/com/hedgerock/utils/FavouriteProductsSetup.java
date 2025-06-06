package com.hedgerock.utils;

import com.hedgerock.feedback.entity.FavouriteProduct;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FavouriteProductsSetup {
    protected static final String USER_ID = "0964775c-82d5-42c6-9f12-246320cef108";

    protected static final FavouriteProduct FIRST_FAVOURITE_PRODUCT = new FavouriteProduct(
            UUID.fromString("57b2e000-044e-4510-bd98-3ea0d6e683b1"), 1L, USER_ID);
    protected static final FavouriteProduct SECOND_FAVOURITE_PRODUCT = new FavouriteProduct(
            UUID.fromString("ea2826f7-6859-4f8e-850a-1eec9319517d"), 2L, USER_ID);
    protected static final FavouriteProduct THIRD_FAVOURITE_PRODUCT = new FavouriteProduct(
            UUID.fromString("ff614597-c9d9-4316-ab3f-34299d4b9cb1"), 3L, USER_ID);

    protected static final FavouriteProduct FOURTH_FAVOURITE_PRODUCT = new FavouriteProduct(
            UUID.fromString("915fdf65-43ea-4247-8376-9e9d5f9001bb"), 4L, USER_ID);

    protected final List<FavouriteProduct> FAVOUIRITE_PRODUCTS_LIST =
            List.of(FIRST_FAVOURITE_PRODUCT, SECOND_FAVOURITE_PRODUCT, THIRD_FAVOURITE_PRODUCT);

    protected String getResponseOfFavouriteProducts() {

        final JSONArray favouriteProductsJson = new JSONArray();

        FAVOUIRITE_PRODUCTS_LIST.forEach(favouriteProduct ->
                insertProductToArray(favouriteProduct, favouriteProductsJson));

        return favouriteProductsJson.toString();
    }

    protected boolean validateFavouriteProductsFields(FavouriteProduct favouriteProduct, Long productId) {
        return Objects.equals(favouriteProduct.getProductId(), productId)
                && favouriteProduct.getId() != null
                && favouriteProduct.getUserId().equals(USER_ID);
    }

    private void insertProductToArray(FavouriteProduct favouriteProduct, JSONArray jsonArray) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("id", favouriteProduct.getId());
            jsonObject.put("productId", favouriteProduct.getProductId());
            jsonObject.put("userId", favouriteProduct.getUserId());

            jsonArray.put(jsonObject);
        } catch (JSONException jsonException) {
            throw new RuntimeException(jsonException.getMessage());
        }
    }

    protected String getErrors() {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            jsonArray.put("Product identification value for favourite catalogue can't be empty");

            jsonObject.put("errors", jsonArray);

            return jsonObject.toString();

        } catch (JSONException exception) {
            throw new RuntimeException(exception.getMessage());
        }

    }

    protected<T> String getSingleIdJSON(T value) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("productId", value);

            return jsonObject.toString();
        } catch (JSONException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    protected String getSingleFavouriteProductJSON(FavouriteProduct favouriteProduct) {
        return getSingleFavouriteProductJSON(favouriteProduct, true);
    }

    protected String getSingleFavouriteProductJSON(FavouriteProduct favouriteProduct, boolean withProductId) {
        try {
            JSONObject jsonObject = new JSONObject();

            if (withProductId) {
                jsonObject.put("id", favouriteProduct.getId());
            }

            jsonObject.put("productId", favouriteProduct.getProductId());
            jsonObject.put("userId", favouriteProduct.getUserId());

            return jsonObject.toString();
        } catch (JSONException jsonException) {
            throw new RuntimeException(jsonException.getMessage());
        }
    }

    protected Mono<JwtAuthenticationToken> getAuthenticationToken() {
        return Mono.just(new JwtAuthenticationToken(Jwt
                .withTokenValue("header.payload.signature")
                .headers(header -> header.put("key", "value"))
                .claim("sub", USER_ID)
                .build()));
    }
}
