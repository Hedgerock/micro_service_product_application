package com.hedgerock.customer.utils.body;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.IntStream;

public class GetProductsCredentials {
    public static final int TOTAL_ITERATIONS = 3;

    public static String getProducts() {
        if (TOTAL_ITERATIONS <= 1) {
            return getProduct().toString();
        }

        JSONArray jsonArray = new JSONArray();

        IntStream.range(0, TOTAL_ITERATIONS).forEach(i -> jsonArray.put(getProduct(i)));

        return jsonArray.toString();
    }

    private static JSONObject getProduct() {
        try {
            final JSONObject jsonObject = new JSONObject();

            jsonObject.put("id", 1);
            jsonObject.put("title", "Test title 1");
            jsonObject.put("details", "Test details 1");

            return jsonObject;
        } catch (JSONException exception) {
            throw new RuntimeException();
        }

    }

    private static JSONObject getProduct(int iteration) {
        final int iterationValue = iteration + 1;

        try {
            final JSONObject jsonObject = new JSONObject();

            jsonObject.put("id", iterationValue);
            jsonObject.put("title", "Test title " + iterationValue);
            jsonObject.put("details", "Test details " + iterationValue);

            return jsonObject;
        } catch (JSONException exception) {
            throw new RuntimeException();
        }

    }

}
