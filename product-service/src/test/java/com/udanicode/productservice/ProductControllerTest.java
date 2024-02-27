package com.udanicode.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.udanicode.productservice.controller.ProductController;
import com.udanicode.productservice.dto.ProductRequest;
import com.udanicode.productservice.dto.ProductResponse;
import com.udanicode.productservice.model.Product;
import com.udanicode.productservice.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.Matchers.hasSize;


@RunWith(MockitoJUnitRunner.class)
public class ProductControllerTest {
    private MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductController productController;

    ProductRequest productRequest1 = new ProductRequest("iPhone12","iPhone12",new BigDecimal(1200));
    ProductRequest productRequest2 = new ProductRequest("iPhone13","iPhone12",new BigDecimal(1200));
    ProductRequest productRequest3 = new ProductRequest("iPhone14","iPhone13",new BigDecimal(1200));

    Product product1 = new Product("1","iPhone12","iPhone12",new BigDecimal(1200));
    Product product2 = new Product("2","iPhone13","iPhone13",new BigDecimal(1200));

    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    public void getAllProductsTest_success()throws Exception{
        List<Product> productList = Arrays.asList(product1,product2);

        Mockito.when(productRepository.findAll()).thenReturn(productList);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("api/product")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) MockMvcResultMatchers.jsonPath("$",hasSize(3)));
    }

}
