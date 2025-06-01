package com.hedgerock.manager.controller;

import com.hedgerock.manager.client.ProductRestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BatyaControllerTest {

    @Mock
    ProductRestClient productRestClient;

    @InjectMocks
    BatyaController batyaController;

    @Test
    void addAttributes() {
        var result = batyaController.setPageFolderTitle();
        assertEquals("catalogue", result);
    }

}