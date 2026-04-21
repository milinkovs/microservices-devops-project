package com.example.product_service;

import com.example.product_service.controller.ProductController;
import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProduct_ShouldReturnSavedProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Gaming laptop");
        product.setPrice(1200.0);

        when(productRepository.save(product)).thenReturn(product);

        ResponseEntity<Product> response = productController.createProduct(product);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Laptop", response.getBody().getName());

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void getProduct_WhenProductExists_ShouldReturnProduct() {
        Long productId = 1L;

        Product product = new Product();
        product.setId(productId);
        product.setName("Laptop");
        product.setDescription("Gaming laptop");
        product.setPrice(1200.0);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ResponseEntity<Product> response = productController.getProduct(productId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().getId());

        verify(productRepository, times(1)).findById(productId);
    }
}
