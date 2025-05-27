package com.hedgerock.catalogue.utils;

import com.hedgerock.catalogue.entity.Product;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class RepositoryUtils {
    private static final String[] TITLES = {"title1","title2","title3","title4","title5","title6","title7","title8"};
    private static final String[] DETAILS = {"detail1","detail2","detail3","detail4","detail5","detail6","detail7","detail8"};
    public static final Random RANDOM = new Random();

    private static final int INIT_VALUE = 0;
    private static final int MAX_VALUE = Math.round(( (float) (TITLES.length + DETAILS.length) / 2 ) - 1);

    public static void initValues(List<Product> products) {
        IntStream.range(INIT_VALUE, MAX_VALUE).forEach(i -> {
            int nextTitleInt = RANDOM.nextInt(INIT_VALUE, MAX_VALUE);
            int nextDetailsInt = RANDOM.nextInt(INIT_VALUE, MAX_VALUE);

            while (nextTitleInt == nextDetailsInt) {
                nextDetailsInt = RANDOM.nextInt(INIT_VALUE, MAX_VALUE);
            }

            products.add(new Product((long) i, TITLES[nextTitleInt], DETAILS[nextDetailsInt]));
        });
    }

    private RepositoryUtils () {}
}
