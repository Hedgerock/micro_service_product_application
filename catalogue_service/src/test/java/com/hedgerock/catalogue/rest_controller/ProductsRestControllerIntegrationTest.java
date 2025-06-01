package com.hedgerock.catalogue.rest_controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;


import java.util.Locale;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProductsRestControllerIntegrationTest {
    private static final String DOCUMENTATION_PATH = "catalogue/products/%s";

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/products.sql")
    void findProducts_ReturnProductList() throws Exception {

        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products")
                .param("title", "Mock")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        //when
            this.mockMvc.perform(requestBuilder)
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                            content().json("""
                             [
                                {"id": 5, "title": "Mock #1", "details":  "This is a details1"},
                                {"id": 9, "title": "Mock #3", "details":  "This is a details3"}
                              ]""")
                    ).andDo(document(
                            DOCUMENTATION_PATH.formatted("find_products_collection"),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("[].id")
                                            .description("Product identification value")
                                            .type("Long"),
                                    fieldWithPath("[].title")
                                            .description("Product title")
                                            .type("String"),
                                    fieldWithPath("[].details")
                                            .description("Product details")
                                            .type("String"),
                                    fieldWithPath("[].updatePayload.title")
                                            .description("Value which will be using for update title of current product")
                                            .type("String"),
                                    fieldWithPath("[].updatePayload.details")
                                            .description("Value which will be using for update details of current product")
                                            .type("String")
                            )
                    ));

        //then

    }

    @Test
    void createProduct_RequestValid_ReturnsNewProduct() throws Exception {
       var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
               .contentType(MediaType.APPLICATION_JSON)
               .content("""
                        {
                           "title": "One more new product",
                           "details":  "Some new description"
                        }
                        """)
               .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

       this.mockMvc.perform(requestBuilder)
               .andDo(print())
               .andExpectAll(
                       status().isCreated(),
                       header().string(HttpHeaders.LOCATION, "http://localhost:8080/catalogue-api/products/1"),
                       content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                       content().json("""
                                        {
                                          "id": 1,
                                          "title": "One more new product",
                                          "details":  "Some new description"
                                        }
                                      """)
               ).andDo(document(
                        DOCUMENTATION_PATH.formatted("create_products"),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title")
                                        .description("Title for new product (Can't be null, blank, less than 3 symbols and more than 50")
                                        .type("String"),
                                fieldWithPath("details")
                                        .description("Details for new product (Can't exceed more than 1000 symbols, can be null or blank)")
                                        .type("String")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .description("Product identification value")
                                        .type("Long"),
                                fieldWithPath("title")
                                        .description("Product title")
                                        .type("String"),
                                fieldWithPath("details")
                                        .description("Product details")
                                        .type("String"),
                                fieldWithPath("updatePayload.title")
                                        .description("Value which will be using for update title of current product")
                                        .type("String"),
                                fieldWithPath("updatePayload.details")
                                        .description("Value which will be using for update details of current product")
                                        .type("String")
                        )
               ));
    }

    @Test
    void createProduct_RequestIsInvalid_ReturnsBadRequestException() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "On", "details":  null}
                        """)
                .locale(new Locale("ua", "UA"))
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                      {"errors":  ["Size of product title must be in range between 3 and 50"]}
                                      """)
                );
    }

    @Test
    void createProduct_UserIsNotAuthorize_ReturnsForbidden() throws  Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "One more new product", "details":  "Some new description"}
                        """)
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }
}