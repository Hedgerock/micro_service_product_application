package com.hedgerock.customer.utils.body;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.IntStream;

public class ProductsFavouriteCredentials {
    private static final String FAVOURITE_FIELD_ID = "id";
    private static final String FAVOURITE_FIELD_PRODUCT_ID = "productId";
    private static final String FAVOURITE_FIELD_USER_ID = "userId";

    private static final String DEFAULT_FAVOURITE_UUID = "c744181f-d530-4bad-bdf3-9c2eeacc2065";
    private static final String DEFAULT_FAVOURITE_USER_UUID = "53efc437-a1e3-4662-a342-74645885c3ad";

    private static final String[] FAVOURITE_PRODUCTS_ID =
            {"e70d6e49-28c3-443e-946a-5a37e4b4a096", "cfba8403-71c0-4fbe-b1c9-410a9d8ec99b"};

    private static final String[] USER_ID =
            {"fe0d895b-3315-4123-be4f-c0232d336e57", "05d58e28-d912-498d-8116-2184799d7094"};

    public static String getFavouriteProducts(boolean isSingle) {
        if (isSingle) {
            return getFavouriteProduct(FAVOURITE_PRODUCTS_ID[0], 1, USER_ID[0]).toString();
        }

        JSONArray jsonArray = new JSONArray();

        IntStream.range(0, FAVOURITE_PRODUCTS_ID.length - 1).forEach(iteration -> {
            String id = FAVOURITE_PRODUCTS_ID[iteration];
            int productId = iteration + 1;
            String userId = USER_ID[iteration];

            jsonArray.put(getFavouriteProduct(id, productId, userId));
        });

        return jsonArray.toString();
    }

    public static String getFavouriteProducts() {
        return getFavouriteProducts(false);
    }

    private static JSONObject getFavouriteProduct(String id, int productId, String userId) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("id", id);
            jsonObject.put("productId", productId);
            jsonObject.put("userId", userId);

            return jsonObject;

        } catch (JSONException exception) {
            throw new RuntimeException();
        }
    }

    public static String getFavouriteCredentials() {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(FAVOURITE_FIELD_ID, DEFAULT_FAVOURITE_UUID);
            jsonObject.put(FAVOURITE_FIELD_PRODUCT_ID, 1);
            jsonObject.put(FAVOURITE_FIELD_USER_ID, DEFAULT_FAVOURITE_USER_UUID);

            return jsonObject.toString();
        } catch (JSONException exception) {
            throw new RuntimeException();
        }
    }
}
