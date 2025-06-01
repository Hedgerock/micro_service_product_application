package com.hedgerock.manager.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.hedgerock.manager.entities.Product;
import com.hedgerock.manager.payload.NewProductPayload;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@WireMockTest(httpPort = 54321)
class ProductsControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getNewProductPage_IsUnauthorized_ReturnsProductPage() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/create")
                .with(user("hedgerock"));

        //when

        this.mockMvc.perform(requestBuilder)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }


    @Test
    void getNewProductPage_Success_ReturnsProductPage() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/create")
                .with(user("hedgerock").roles("MANAGER"));

        //when

        this.mockMvc.perform(requestBuilder)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("core"),
                        model().attribute("newProduct", new NewProductPayload(null, null)),
                        model().attribute("pageTitle", "New product creation page"),
                        model().attribute("errors", new ArrayList<>())
                );
    }

    @Test
    void getProductsList_ReturnListOfProducts() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/list")
                .queryParam("title", "product")
                .with(user("hedgerock").roles("MANAGER"));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("title", WireMock.equalTo("product"))
                .willReturn(WireMock.ok("""
                       [
                            {"id": 1, "title": "Product #1", "details": "Description #1"},
                            {"id": 2, "title": "Product #2", "details": "Description #2"}
                       ]
                       """).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        ));

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("core"),
                        model().attribute("pageTitle", "Products list"),
                        model().attribute("title", "product"),
                        model().attribute("products", List.of(
                            new Product(1, "Product #1", "Description #1"),
                            new Product(2, "Product #2", "Description #2")
                        ))

                );

        WireMock.verify(WireMock
                .getRequestedFor(WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("title", WireMock.equalTo("product"))
        );
    }
}
