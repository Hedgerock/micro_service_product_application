package com.hedgerock.manager.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.hedgerock.manager.payload.NewProductPayload;
import com.hedgerock.manager.payload.UpdateProductPayload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 54321)
class ProductControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void deleteProduct_ProductExists_RedirectsToProductsListPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/products/delete/1")
                .with(user("hedgerock").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id": 1,
                            "title": "Product",
                            "details": "Product description"
                        }
                        """)));

        WireMock.stubFor(WireMock.delete("/catalogue-api/products/1")
                .willReturn(WireMock.noContent()));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/catalogue/products/list")
                );

        WireMock.verify(WireMock.deleteRequestedFor(WireMock.urlPathMatching("/catalogue-api/products/1")));
    }

    @Test
    void getCurrentProduct_UserIsUnauthorized_get403Status() throws Exception{

        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/list/1")
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
    void getCurrentProduct_ProductNotFound_RedirectToTheNotFoundPage() throws Exception{

        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/list/1")
                .with(user("hedgerock").roles("MANAGER"));

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isFound(),
                        redirectedUrl("/not-found-page")
                );

    }

    @Test
    void getCurrentProduct_ProductFound_ReturnCurrentProductView() throws Exception{

        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/list/1")
                .with(user("hedgerock").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id": 1,
                            "title": "Test product",
                            "details": "Product details"
                        }
                        """)));

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("core"),
                        model().attribute("pageTitle", "Test product - page")
                );

        WireMock.verify(WireMock.
                getRequestedFor(WireMock.urlPathMatching("/catalogue-api/products/1")));
    }

    @Test
    void updateProduct_UserIsNotAuthorized_Forbidden() throws Exception{

        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/edit/1")
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
    void updateProduct_ProductNotFound_RedirectToNotFoundPage() throws Exception{

        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/edit/1")
                .with(user("hedgerock").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1").willReturn(
                WireMock.notFound()
        ));

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isFound()
                );

        WireMock.verify(WireMock.
                getRequestedFor(WireMock.urlPathMatching("/catalogue-api/products/1")));
    }

    @Test
    void updateProduct_ProductFound_ReturnViewForEdit() throws Exception{

        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/edit/1")
                .with(user("hedgerock").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id": 1,
                            "title": "Test product",
                            "details": "Product details"
                        }
                        """)));

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("core"),
                        model().attribute("pageTitle", "Test product update page"),
                        model().attribute("productId", 1L),
                        model().attribute("newProduct", new UpdateProductPayload("Test product", "Product details"))
                );

        WireMock.verify(WireMock.
                getRequestedFor(WireMock.urlPathMatching("/catalogue-api/products/1")));
    }
}