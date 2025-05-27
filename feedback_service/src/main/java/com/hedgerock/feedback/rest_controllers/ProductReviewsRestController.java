package com.hedgerock.feedback.rest_controllers;

import com.hedgerock.feedback.entity.ProductReview;
import com.hedgerock.feedback.rest_controllers.payload.NewProductReviewPayload;
import com.hedgerock.feedback.service.product_review_option_service.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("feedback-api/product-reviews")
public class ProductReviewsRestController {

    private final ProductReviewService productReviewService;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @GetMapping("by-product-id/{productId:\\d+}")
    public Flux<ProductReview> getProductReviewsByProductId(
//            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @PathVariable("productId") Long productId) {

        //Option to get value by reactiveMongoTemplate
//    return authenticationTokenMono.flatMapMany(token -> this.reactiveMongoTemplate.find(
//            Query.query(Criteria.where("productId").is(productId).and("userId").is(token.getToken().getSubject())),
//            ProductReview.class
//    ));

//        //By repository
//         return authenticationTokenMono.flatMapMany(token ->
//            this.productReviewService.findProductReviewsByProduct(productId, token.getToken().getSubject()));

        return this.productReviewService.findProductReviewsByProduct(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createNewProductReview(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            UriComponentsBuilder uriComponentsBuilder,
            @Valid @RequestBody Mono<NewProductReviewPayload> payloadMono
    ) {
        return authenticationTokenMono.flatMap(token -> payloadMono
                .flatMap(payload -> this.productReviewService.addReview(
                        payload.productId(), payload.rating(), payload.review(), token.getToken().getSubject()))
                .map(review -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("/feedback-api/product-reviews/{id}")
                                .build(review.getId()))
                        .body(review)));
    }

}
