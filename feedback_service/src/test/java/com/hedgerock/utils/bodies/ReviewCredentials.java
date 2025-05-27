package com.hedgerock.utils.bodies;

import com.hedgerock.feedback.entity.ProductReview;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.reactive.function.client.ClientRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
public class ReviewCredentials {
    private static final Long PRODUCT_ID = 1L;
    private static final Integer RATING_VALUE = 5;
    private static final String REVIEW_VALUE = "Test of creation review";

    public static List<ProductReview> getReviews() {
        return List.of(
                new ProductReview(
                        UUID.fromString("eb02e46a-760e-48aa-9393-01997e4e7e21"), PRODUCT_ID, 1, "Test review 1", "test-user-1"),
                new ProductReview(
                        UUID.fromString("54357d56-7c57-4f80-a065-6ec674df6de7"), PRODUCT_ID, 3, "Test review 2", "test-user-2"),
                new ProductReview(
                        UUID.fromString("2f0f7cc8-b72b-44c7-99b4-c0b2f8c9e0f0"), PRODUCT_ID, 5, "Test review 3", "test-user-3")
        );
    }

    public static Mono<ClientRequest> initClientRequest(ClientRequest clientRequest) {
        log.info("=============REQUEST=============");
        log.info("{} {}", clientRequest.method(), clientRequest.url());
        clientRequest.headers().forEach((header, value) -> log.info("{}: {}", header, value));
        log.info("=============END_REQUEST=============");

        return Mono.just(clientRequest);
    }

    public static String getCreateReviewBody() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("productId", PRODUCT_ID);
            jsonObject.put("rating", RATING_VALUE);
            jsonObject.put("review", REVIEW_VALUE);

            return jsonObject.toString();
        } catch (JSONException e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    public static String getCreateReviewBody(boolean makeBadRequest) {
        if (!makeBadRequest) {
            return getCreateReviewBody();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("productId", null);
            jsonObject.put("rating", -1);
            jsonObject.put("review", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec malesuada orci fringilla nulla convallis, eu dapibus orci scelerisque. Nulla sed massa ultrices nunc vulputate posuere at non ipsum. Praesent rhoncus finibus tempus. Etiam posuere efficitur purus non varius. Vivamus quam risus, varius a dictum nec, congue ac neque. Sed diam nisi, placerat ut dolor et, ullamcorper dignissim mi. Suspendisse potenti. Suspendisse pretium ligula in molestie auctor.Nam nisl orci, auctor non tortor at, mollis ornare massa. Vestibulum at tortor dignissim, viverra velit ac, suscipit elit. Nam consequat purus eget ipsum laoreet, a tincidunt odio porta. Suspendisse potenti. Phasellus quis mi mattis, finibus orci nec, mattis augue. Vivamus luctus lacus mauris, sed convallis elit commodo non. Donec vitae lectus varius, molestie massa sed, varius ante. Pellentesque et euismod lectus.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec malesuada orci fringilla nulla convallis, eu dapibus orci scelerisque. Nulla sed massa ultrices nunc vulputate posuere at non ipsum. Praesent rhoncus finibus tempus. Etiam posuere efficitur purus non varius. Vivamus quam risus, varius a dictum nec, congue ac neque. Sed diam nisi, placerat ut dolor et, ullamcorper dignissim mi. Suspendisse potenti. Suspendisse pretium ligula in molestie auctor.\\n\" +\n" + "\\n\" +\n" + "Nam nisl orci, auctor non tortor at, mollis ornare massa. Vestibulum at tortor dignissim, viverra velit ac, suscipit elit. Nam consequat purus eget ipsum laoreet, a tincidunt odio porta. Suspendisse potenti. Phasellus quis mi mattis, finibus orci nec, mattis augue. Vivamus luctus lacus mauris, sed convallis elit commodo non. Donec vitae lectus varius, molestie massa sed, varius ante. Pellentesque et euismod lectus.");

            return jsonObject.toString();
        } catch (JSONException e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    public static String getCreateReviewBody(String userId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("productId", PRODUCT_ID);
            jsonObject.put("rating", RATING_VALUE);
            jsonObject.put("review", REVIEW_VALUE);

            if (userId != null && !userId.isBlank()) {
                jsonObject.put("userId", userId);
            }

            return jsonObject.toString();
        } catch (JSONException e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    public static String getErrors() {
        try {
            JSONObject jsonObject = new JSONObject();

            JSONArray jsonArray = new JSONArray();
            jsonArray.put("Product identification value for review can't be empty");
            jsonArray.put("Rating can't be less than 1 your value is 1");
            jsonArray.put("Review message can't be more than 1000 symbols");

            jsonObject.put("errors", jsonArray);

            return jsonObject.toString();
        } catch (JSONException e) {
            throw new RuntimeException("Something went wrong");
        }
    }

    public static String getSearchReviewBody() {
        return (
                """
                      [
                        {
                            "id": "eb02e46a-760e-48aa-9393-01997e4e7e21",
                            "productId": 1,
                            "rating": 1,
                            "review": "Test review 1",
                            "userId": "test-user-1"
                        },
                        {
                             "id": "54357d56-7c57-4f80-a065-6ec674df6de7",
                             "productId": 1,
                             "rating": 3,
                             "review": "Test review 2",
                             "userId": "test-user-2"
                        },
                        {
                              "id": "2f0f7cc8-b72b-44c7-99b4-c0b2f8c9e0f0",
                              "productId": 1,
                              "rating": 5,
                              "review": "Test review 3",
                              "userId": "test-user-3"
                        }
                      ]
                 """
        );
    }

}
