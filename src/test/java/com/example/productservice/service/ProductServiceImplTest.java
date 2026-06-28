package com.example.productservice.service;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.entity.Product;
import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.mapper.ProductMapper;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequest request;
    private ProductResponse response;

    @BeforeEach
    void setUp() {
        request = new ProductRequest();
        request.setName("Laptop");
        request.setUnitPrice(new BigDecimal("999.99"));
        request.setDescription("A powerful laptop");
        request.setCategory("Electronics");
        request.setStock(50);

        product = Product.builder()
                .productId(1L)
                .name("Laptop")
                .unitPrice(new BigDecimal("999.99"))
                .description("A powerful laptop")
                .category("Electronics")
                .stock(50)
                .build();

        response = ProductResponse.builder()
                .productId(1L)
                .name("Laptop")
                .unitPrice(new BigDecimal("999.99"))
                .description("A powerful laptop")
                .category("Electronics")
                .stock(50)
                .build();
    }

    @Test
    void getAllProducts_returnsEmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result).isEmpty();
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getAllProducts_returnsList() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void createProduct_success() {
        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse result = productService.createProduct(request);

        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductById_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(1L);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_throwsProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteProduct_success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_throwsProductNotFoundException() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.deleteProduct(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).deleteById(any());
    }
}