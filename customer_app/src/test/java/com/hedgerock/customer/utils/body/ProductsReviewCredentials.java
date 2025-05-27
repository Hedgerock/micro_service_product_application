package com.hedgerock.customer.utils.body;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.IntStream;

@Slf4j
public class ProductsReviewCredentials {
    private static final String REVIEW_FIELD_ID = "id";
    private static final String REVIEW_FIELD_PRODUCT_ID = "productId";
    private static final String REVIEW_FIELD_RATING = "rating";
    private static final String REVIEW_FIELD_REVIEW = "review";
    private static final String REVIEW_FIELD_USER_ID = "userId";

    public static final String DEFAULT_REVIEW_UUID = "595d4e5a-cbc1-11ee-864f-8fb72674cca";
    private static final String DEFAULT_USER_UUID = "5da9bf2a-cbc1-11ee-a8a7-d355f5a3dd8e";
    private static final int DEFAULT_REVIEW_RATING = 5;
    private static final String DEFAULT_REVIEW_VALUE = "Review number ";

    public static final String VERY_GIANT_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In lacinia imperdiet est commodo aliquam. Fusce mollis quam nibh, at vestibulum ipsum porta eget. In hac habitasse platea dictumst. Cras tristique sem erat, quis eleifend enim vestibulum eget. Curabitur tincidunt libero in neque vestibulum, in lobortis nisi efficitur. Morbi ac dolor lorem. Nullam dapibus dui metus, vel vestibulum diam faucibus vel. Nunc posuere porta nunc. Etiam quis condimentum mi. Aliquam quis enim lacus. Sed et leo vitae leo iaculis cursus. Praesent eget sodales risus, at commodo ipsum. Curabitur aliquet ut lectus eu tempus. Proin vel feugiat metus, quis varius augue. Morbi dictum dignissim efficitur Nam at sem enim. Aliquam erat volutpat. Nunc at sapien eget eros fringilla gravida. Curabitur iaculis porttitor sapien vitae vestibulum. Cras ornare pulvinar libero, non feugiat nulla mattis sit amet. In non sem nec diam dignissim auctor. Curabitur pharetra justo et neque convallis, vitae rhoncus ipsum dignissim. Fusce eleifend placerat arcu, ut fermentum risus consectetur vel. Suspendisse et vulputate nibh. Aliquam faucibus risus eget dui tincidunt congue. Nulla varius condimentum dolor. Donec rutrum volutpat est ac tincidunt. Nullam posuere urna pharetra dapibus rutrum. Quisque congue nisi ante, a dignissim erat semper vel. Suspendisse in pharetra ipsum, sit amet viverra erat. Sed consequat quam sed laoreet accumsan. Maecenas a est molestie, suscipit justo eu, pellentesque ex. In hac habitasse platea dictumst. Aliquam quam arcu, sagittis in consequat at, eleifend a quam. Cras tincidunt ante at justo sollicitudin molestie. Nullam quis tincidunt massa, ac tincidunt massa. Aliquam ut consectetur tortor, nec maximus sem. Praesent nec dignissim justo. In sollicitudin orci non felis ultricies, ut sollicitudin diam tincidunt. Pellentesque a mi id leo varius porttitor. Curabitur nec est pharetra nisl egestas luctus non eget orci. Aenean sodales, enim in suscipit mattis, diam mauris tristique ante, eget accumsan urna elit nec ligula. Pellentesque faucibus hendrerit facilisis. In efficitur sapien sit amet libero scelerisque dapibus Pellentesque egestas a ligula vel varius. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Pellentesque a dolor a lacus ornare placerat nec iaculis lectus. Phasellus tincidunt velit vel lacus iaculis viverra. Morbi id metus dapibus, auctor lacus et, ullamcorper nisi. Morbi vestibulum, quam et luctus venenatis, turpis quam congue eros, a porttitor felis libero in enim. Sed ut quam sodales, commodo lectus nec, ullamcorper eros. Maecenas pulvinar enim eget porttitor pharetra. Maecenas efficitur dolor at dui fringilla maximus id a leo. Donec faucibus porttitor magna. Donec vitae pharetra mauris, ac porta massa. Quisque ullamcorper ipsum in lacinia porta. Curabitur at magna nunc. Aenean congue urna sit amet rhoncus eleifend Quisque placerat nisl id urna varius molestie. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed arcu velit, sollicitudin a rhoncus nec, porttitor eu elit. Nunc a tristique purus. Aenean sagittis, mauris ut dapibus gravida, lacus erat bibendum lorem, in volutpat dolor mauris eu tellus. Pellentesque varius tellus ante, non tristique dui egestas eget. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed sodales nulla sit amet consectetur lobortis. Vivamus interdum augue quis gravida dapibus. Proin efficitur maximus mi id viverra. Vivamus mauris purus, tincidunt sed tempus imperdiet, ullamcorper vitae lorem. Nam fringilla magna laoreet mauris sagittis pellentesque. Ut sollicitudin, leo non facilisis aliquet, dolor ipsum malesuada sapien, eget bibendum libero metus non quam. Pellentesque diam diam, consectetur eget sapien quis, venenatis porttitor urna. Mauris vel sapien sit amet leo condimentum tempor sed ac enim. Fusce feugiat et ante eget imperdiet. Donec et felis luctus nisi bibendum consectetur vitae facilisis nunc. Fusce volutpat tempus eros, ac ultrices mi rhoncus quis. Fusce ultricies, quam vitae placerat laoreet, tortor dui vestibulum nunc, sit amet dictum ipsum ligula a dui. Morbi finibus volutpat tortor ut vulputate. Maecenas lorem lectus, maximus ac ex ac, consequat mattis magna. Integer fermentum magna purus, id malesuada augue ultricies nec. Pellentesque tortor dui, porta a rutrum a, iaculis nec massa. Nam scelerisque lacus ipsum, vitae tempor ligula facilisis nec. Curabitur arcu elit, iaculis sit amet semper ac, faucibus ac nisl. Suspendisse neque eros, gravida quis purus sit amet, faucibus volutpat justo. Duis molestie, nibh non rutrum vestibulum, sem tellus malesuada nisi, et gravida ipsum nunc vitae lectus. Nulla luctus eros in mi vestibulum rutrum. Phasellus vulputate leo in molestie pulvinar. Maecenas justo lectus, venenatis posuere dapibus ut, fringilla ut neque. Donec leo velit, finibus pulvinar quam a, sagittis porta ex. Pellentesque sed ante cursus, dignissim tellus et, feugiat ante. Duis in aliquam dolor. Donec feugiat augue vel velit accumsan, sit amet blandit mi gravida. Nulla elementum augue at erat sollicitudin, eu tristique urna molestie. Pellentesque tempus nisl sed arcu suscipit porta. Fusce vulputate est in blandit egestas. Morbi tempor congue arcu id efficitur. Morbi imperdiet magna id erat vulputate, ut tristique nisi rhoncus. Donec convallis nec dui et porta. Maecenas quis est venenatis, accumsan ligula ac, lacinia nunc. Vestibulum commodo cursus nibh eget bibendum. Suspendisse et convallis erat. Ut venenatis interdum pulvinar. Vivamus malesuada vulputate turpis a tempus. Nulla at orci euismod, lobortis felis eu, fringilla eros. Sed euismod sed arcu nec molestie. Donec molestie quis dui vitae volutpat. Sed venenatis tellus vel enim dapibus lacinia. Praesent in viverra augue. In tempus elit nibh. Morbi aliquet venenatis elit. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Maecenas ut congue libero. Nam fermentum tempus vehicula. Curabitur sed sapien non neque gravida accumsan non eget turpis. Aliquam blandit, justo ac congue commodo, turpis mauris mattis dui, quis congue nulla sapien a arcu. Nullam consequat mauris nisl, sit amet mattis libero tincidunt sed. Etiam vestibulum sed mi eget bibendum. Aenean luctus enim et mi pharetra, pulvinar blandit massa luctus. Nullam facilisis ut lectus sit amet aliquam. Nulla viverra quam in aliquet semper. Maecenas erat magna, hendrerit et placerat feugiat, interdum quis neque. Aenean eleifend sodales tempus. Integer fringilla scelerisque erat, a lobortis magna finibus quis. Donec cursus dapibus semper. Nulla vitae erat eu ligula scelerisque pulvinar id sed erat. Maecenas cursus sapien et orci varius, ac ultrices lectus aliquet. Praesent condimentum nibh vitae mi dignissim, a semper mi blandit. Suspendisse non elit augue. Fusce ipsum libero, mattis vel ornare vel, congue in sapien. Proin viverra odio eu justo dapibus condimentum. Sed sapien felis, maximus ac mollis in, dignissim et purus. Proin pharetra eu tellus sed efficitur. Aliquam tristique vitae justo sagittis tempus. Proin lobortis augue metus, vel venenatis ligula accumsan eget. In ac arcu lorem. Nam posuere condimentum ex, at suscipit tellus cursus a. Aliquam hendrerit ex sit amet finibus dignissim. Nunc bibendum id metus sed laoreet. Vestibulum sit amet ante sit amet dui sagittis tristique eget in ipsum. Suspendisse laoreet velit nulla, eget feugiat libero eleifend vel. Duis blandit ipsum vel sapien tincidunt, in lobortis orci volutpat.";

    public static String getProductReview(int totalReviews) {
        if (totalReviews <= 1) {
           try {
               return getProductReview();
           } catch (JSONException exception) {
               log.error(exception.getMessage(), exception);
               throw new RuntimeException();
           }
        }

        JSONArray jsonArray = new JSONArray();

        IntStream.range(0, totalReviews).forEach(iteration -> getProductReview(jsonArray, iteration, false));

        return jsonArray.toString();
    }

    public static String getProductReview(int totalReviews, boolean isNew) {
        if (!isNew) {
            return getProductReview(totalReviews);
        }

        if (totalReviews <= 1) {
            try {
                return getSingleProductReview();
            } catch (JSONException exception) {
                log.error(exception.getMessage(), exception);
                throw new RuntimeException();
            }
        }

        JSONArray jsonArray = new JSONArray();

        IntStream.range(0, totalReviews).forEach(iteration -> getProductReview(jsonArray, iteration, true));

        return jsonArray.toString();
    }

    private static void getProductReview(JSONArray jsonArray, int iteration, boolean isNew) {
        try {
            JSONObject jsonObject = getCurrentProductReview(iteration, isNew);
            jsonArray.put(jsonObject);
        } catch (JSONException exception) {
            log.error(exception.getMessage(), exception);
            throw new RuntimeException();
        }
    }

    private static JSONObject getCurrentProductReview(int iteration, boolean isNew) throws JSONException {
        if (!isNew) {
            return getCurrentProductReview(iteration);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(REVIEW_FIELD_PRODUCT_ID, 1);

        int currentRating = (iteration % DEFAULT_REVIEW_RATING) + 1;
        jsonObject.put(REVIEW_FIELD_RATING, currentRating);
        jsonObject.put(REVIEW_FIELD_REVIEW, DEFAULT_REVIEW_VALUE + (iteration + 1));

        return jsonObject;
    }

    public static String getSingleProductReview() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(REVIEW_FIELD_PRODUCT_ID, 1);

        jsonObject.put(REVIEW_FIELD_RATING, DEFAULT_REVIEW_RATING);
        jsonObject.put(REVIEW_FIELD_REVIEW, DEFAULT_REVIEW_VALUE + 1);

        return jsonObject.toString();
    }

    private static JSONObject getCurrentProductReview(int iteration) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(REVIEW_FIELD_ID, DEFAULT_REVIEW_UUID + iteration);
        jsonObject.put(REVIEW_FIELD_PRODUCT_ID, 1);

        int currentRating = (iteration % DEFAULT_REVIEW_RATING) + 1;
        jsonObject.put(REVIEW_FIELD_RATING, currentRating);
        jsonObject.put(REVIEW_FIELD_REVIEW, DEFAULT_REVIEW_VALUE + (iteration + 1));
        jsonObject.put(REVIEW_FIELD_USER_ID, DEFAULT_USER_UUID + iteration);

        return jsonObject;
    }

    public static String getProductReview() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(REVIEW_FIELD_ID, DEFAULT_REVIEW_UUID + 1);
        jsonObject.put(REVIEW_FIELD_PRODUCT_ID, 1);

        jsonObject.put(REVIEW_FIELD_RATING, DEFAULT_REVIEW_RATING);
        jsonObject.put(REVIEW_FIELD_REVIEW, DEFAULT_REVIEW_VALUE + 1);
        jsonObject.put(REVIEW_FIELD_USER_ID, DEFAULT_USER_UUID + 1);

        return jsonObject.toString();
    }

    public static String getInvalidCredentials() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(REVIEW_FIELD_PRODUCT_ID, 1);

            jsonObject.put(REVIEW_FIELD_RATING, -1);
            jsonObject.put(REVIEW_FIELD_REVIEW, "Super giant text");

            return jsonObject.toString();
        } catch (JSONException exception) {
            throw new RuntimeException();
        }
    }

    public static String getErrors() {
        try {
            JSONObject jsonObject = new JSONObject();

            JSONArray jsonArray = new JSONArray();

            jsonArray.put("Error 1");
            jsonArray.put("Error 2");
            jsonArray.put("Error 3");

            jsonObject.put("errors", jsonArray);

            return jsonObject.toString();
        } catch (JSONException exception) {
            throw new RuntimeException();
        }
    }

}
